package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.service.model.HydraClient;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Service
public class OidcService {
    private static final int LIMIT = 500;

    private final RestTemplate restTemplate;

    @Value("${tara-oidc.url}")
    private String baseUrl;

    public OidcService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<HydraClient> getAllClients(String clientId) throws FatalApiException {
        if (clientId != null) {
            HydraClient client = this.getClientByClientId(clientId);
            return client == null ? List.of() : List.of(client);
        } else {
            return getAllHydraClients();
        }
    }

    private HydraClient getClientByClientId(String clientId) throws FatalApiException {
        String uri = String.format("%s/clients/%s", baseUrl, clientId);

        log.info("Sending " + HttpMethod.GET.name() + " request to OIDC service.", value("url.full", uri));
        try {
            ResponseEntity<HydraClient> response = restTemplate.getForEntity(uri, HydraClient.class);

            if (log.isDebugEnabled()) {
                log.debug("OIDC response: {}", value("response", response));
            }

            return response.getBody();
        } catch (HttpClientErrorException ex) {
            log.error(String.format("Hydra request: GET %s failed.", uri), ex);
            log.warn("Expecting failure to be caused by not finding client with client_id: " + clientId);
            return null;
        } catch (HttpServerErrorException ex) {
            log.error(String.format("Hydra request: GET %s failed.", uri), ex);
            throw new FatalApiException("Oidc.serverError");
        }
    }

    public void deleteClient(String clientId) throws ApiException {
        String uri = String.format("%s/clients/%s", baseUrl, clientId);

        try {
            log.info("Sending " + HttpMethod.DELETE.name() + " request to OIDC service.", value("url.full", uri));
            ResponseEntity<Void> response = restTemplate.exchange(uri, HttpMethod.DELETE, null, new ParameterizedTypeReference<Void>() {});

            if (log.isDebugEnabled()) {
                log.debug("OIDC response: {}", value("response", response));
            }

            log.info(String.format("Deleted client with client_id %s.", clientId));
        } catch (HttpClientErrorException ex) {
            log.error(String.format("Hydra request: DELETE %s failed.", uri), ex);
            log.warn("Expecting failure to be caused by not finding client with client_id: " + clientId);
            throw new RecordDoesNotExistException("Client.notExists");
        } catch (HttpServerErrorException ex) {
            log.error(String.format("Hydra request: DELETE %s failed.", uri), ex);
            throw new FatalApiException("Oidc.serverError");
        }
    }

    private List<HydraClient> getAllHydraClients() throws FatalApiException {
        String uri = String.format("%s/clients", baseUrl);
        List<HydraClient> clients = new ArrayList<>();

        while (true) {
            try {
                String queryString = String.format("?offset=%s&limit=%s", clients.size(), LIMIT);

                log.info("Sending " + HttpMethod.GET.name() + " request to OIDC service.", value("url.full", uri + queryString));

                List<HydraClient> response = restTemplate.exchange(uri + queryString, HttpMethod.GET,
                        null, new ParameterizedTypeReference<List<HydraClient>>() {
                        }).getBody();
                clients.addAll(response);

                if (response.size() < LIMIT) {
                    break;
                }
            } catch (HttpServerErrorException ex) {
                log.error(String.format("Hydra request: %s failed.", uri), ex);
                throw new FatalApiException("Oidc.serverError");
            }
        }

        return clients;
    }

    public void saveClient(HydraClient client, String uri, HttpMethod httpMethod) throws ApiException {
        try {
            log.info("Sending " + httpMethod.name() + " request to OIDC service.", value("url.full", uri));
            ResponseEntity<Object> response = restTemplate.exchange(uri, httpMethod, new HttpEntity<>(client), Object.class);

            if (log.isDebugEnabled()) {
                log.debug("OIDC request: {}", value("request", client));
                log.debug("OIDC response: {}", value("response", response));
            }
        } catch (HttpClientErrorException ex) {
            log.error(String.format("Hydra request: %s %s failed.", httpMethod.name(), uri), ex);

            if (ex.getRawStatusCode() == 400) {
                throw new InvalidDataException("Oidc.clientError.400");
            }

            log.warn("Expecting failure to be caused by not finding client with client_id: " + client.getClientId());
            throw new InvalidDataException("Oidc.clientError.409");
        } catch (HttpServerErrorException ex) {
            log.error(String.format("Hydra request: %s %s failed.", httpMethod.name(), uri), ex);
            throw new FatalApiException("Oidc.serverError");
        }
    }
}
