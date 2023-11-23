package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.service.helper.PaginationHelper;
import ee.ria.tara.service.model.HydraClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.Optional;

import static net.logstash.logback.argument.StructuredArguments.value;

@Slf4j
@Service
@RequiredArgsConstructor
public class OidcService {
    private final RestTemplate restTemplate;
    private final TaraOidcConfigurationProvider taraOidcConfigurationProvider;

    public List<HydraClient> getAllClients(String clientId) throws FatalApiException {
        if (clientId != null) {
            HydraClient client = this.getClientByClientId(clientId);
            return client == null ? List.of() : List.of(client);
        } else {
            return getAllHydraClients();
        }
    }

    private HydraClient getClientByClientId(String clientId) throws FatalApiException {
        String uri = String.format("%s/admin/clients/%s", taraOidcConfigurationProvider.getUrl(), clientId);

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
        String uri = String.format("%s/admin/clients/%s", taraOidcConfigurationProvider.getUrl(), clientId);

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
        String uri = String.format("%s/admin/clients", taraOidcConfigurationProvider.getUrl());
        List<HydraClient> clients = new ArrayList<>();
        Optional<String> nextPageTokenEncoded = Optional.of(PaginationHelper.INITIAL_PAGE_TOKEN);
        while (nextPageTokenEncoded.isPresent()) {
            try {
                String queryString = String.format("%s?page_size=%s&page_token=%s", uri, taraOidcConfigurationProvider.getPageSize(), nextPageTokenEncoded.get());
                log.info("Sending " + HttpMethod.GET.name() + " request to OIDC service.", value("url.full", queryString));

                ResponseEntity<List<HydraClient>> response = restTemplate.exchange(queryString, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {
                        });
                clients.addAll(response.getBody());
                nextPageTokenEncoded = PaginationHelper.getNextPageToken(response.getHeaders());
            } catch (HttpServerErrorException ex) {
                log.error(String.format("Hydra request: %s failed.", uri), ex);
                throw new FatalApiException("Oidc.serverError");
            }
        }

        return clients;
    }

    public void saveClient(HydraClient client, String uri, HttpMethod httpMethod) throws ApiException {
        try {
            log.info("Sending " + httpMethod.name() + " request to OIDC service: " + client, value("url.full", uri));
            ResponseEntity<Object> response = restTemplate.exchange(uri, httpMethod, new HttpEntity<>(client), Object.class);

            if (log.isDebugEnabled()) {
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
