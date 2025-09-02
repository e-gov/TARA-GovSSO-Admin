package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.controllers.exception.RecordDoesNotExistException;
import ee.ria.tara.service.helper.PaginationHelper;
import ee.ria.tara.service.model.HydraClient;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.hc.core5.net.URIBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpServerErrorException;
import org.springframework.web.client.RestTemplate;

import java.net.URI;
import java.net.URISyntaxException;
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

    public List<HydraClient> getAllClients() throws FatalApiException {
        List<HydraClient> clients = new ArrayList<>();
        Optional<String> nextPageToken = Optional.of(PaginationHelper.INITIAL_PAGE_TOKEN);
        while (nextPageToken.isPresent()) {
            URI uri = null;
            try {
                uri = new URIBuilder(taraOidcConfigurationProvider.getUrl())
                        .appendPath("/admin/clients")
                        .addParameter("page_size", String.valueOf(taraOidcConfigurationProvider.getPageSize()))
                        .addParameter("page_token", nextPageToken.get())
                        .build();
                log.info("Sending " + HttpMethod.GET.name() + " request to OIDC service.", value("url.full", uri));

                ResponseEntity<List<HydraClient>> response = restTemplate.exchange(uri, HttpMethod.GET, null,
                        new ParameterizedTypeReference<>() {
                        });
                clients.addAll(response.getBody());
                nextPageToken = PaginationHelper.getNextPageToken(response.getHeaders());
            } catch (HttpServerErrorException ex) {
                log.error(String.format("Hydra request: %s failed.", uri.toString()), ex);
                throw new FatalApiException("Oidc.serverError");
            } catch (URISyntaxException e) {
                throw new FatalApiException("Server.error", e);
            }
        }

        return clients;
    }

    public HydraClient getClient(@NonNull String clientId) throws FatalApiException {
        URI uri = null;
        try {
            uri = new URIBuilder(taraOidcConfigurationProvider.getUrl())
                    .appendPath("/admin/clients")
                    .appendPath(clientId)
                    .build();
            log.info("Sending " + HttpMethod.GET.name() + " request to OIDC service.", value("url.full", uri));
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
        } catch (URISyntaxException e) {
            throw new FatalApiException("Server.error", e);
        }
    }

    public void deleteClient(String clientId) throws RecordDoesNotExistException, FatalApiException {
        URI uri = null;
        try {
            uri = new URIBuilder(taraOidcConfigurationProvider.getUrl())
                    .appendPath("/admin/clients")
                    .appendPath(clientId)
                    .build();
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
        } catch (URISyntaxException e) {
            throw new FatalApiException("Server.error", e);
        }
    }

    public void createClient(HydraClient client) {
        try {
            URI uri = new URIBuilder(taraOidcConfigurationProvider.getUrl())
                    .appendPath("/admin/clients")
                    .build();
            saveClient(client, uri, HttpMethod.POST);
        } catch (URISyntaxException e) {
            throw new FatalApiException("Server.error", e);
        }
    }

    public void updateClient(String clientId, HydraClient client) {
        try {
            URI uri = new URIBuilder(taraOidcConfigurationProvider.getUrl())
                    .appendPath("/admin/clients")
                    .appendPath(clientId)
                    .build();
            saveClient(client, uri, HttpMethod.PUT);
        } catch (URISyntaxException e) {
            throw new FatalApiException("Server.error", e);
        }
    }

    private void saveClient(HydraClient client, URI uri, HttpMethod httpMethod) throws InvalidDataException, FatalApiException {
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
