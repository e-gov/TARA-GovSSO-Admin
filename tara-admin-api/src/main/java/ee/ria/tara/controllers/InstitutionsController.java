package ee.ria.tara.controllers;

import ee.ria.tara.api.InstitutionsApi;
import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.InstitutionsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import javax.validation.ValidationException;
import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.List;

@Slf4j
@RestController
public class InstitutionsController implements InstitutionsApi {
    private final HttpServletRequest request;

    private final InstitutionsService institutionsService;
    private final ClientsService clientsService;
    private final AdminConfigurationProvider adminConfigurationProvider;

    public static final String ERR_BACKCHANNEL_URI = "Taustakanali väljalogimise URL on kohustuslik, peab kasutama https protokolli ja ei tohi sisaldada userinfo ega fragment osi.";
    public static final String ERR_REDIRECT_URI = "Lubatud autentimise tagasisuunamispäringu URL on kohustuslik, peab kasutama https protokolli ja ei tohi sisaldada userinfo ega fragment osi.";
    public static final String ERR_POST_LOGOUT_REDIRECT_URI = "Lubatud väljalogimise tagasisuunamispäringu URL on kohustuslik, peab kasutama https protokolli ja ei tohi sisaldada userinfo ega fragment osi.";

    public InstitutionsController(InstitutionsService institutionsService, ClientsService clientsService, HttpServletRequest request, AdminConfigurationProvider adminConfigurationProvider) {
        this.institutionsService = institutionsService;
        this.clientsService = clientsService;
        this.request = request;
        this.adminConfigurationProvider = adminConfigurationProvider;
    }

    @Override
    public ResponseEntity<List<Client>> getAllInstitutionClients(String registryCode) {
        log.info(String.format("Incoming request: GET %s.", request.getRequestURI()));
        return ResponseEntity.ok(clientsService.getAllInstitutionsClients(registryCode));
    }

    @Override
    public ResponseEntity<Void> addClientToInstitution(String registryCode, @Valid Client client) {
        log.info(String.format("Incoming request: POST %s, with client_id: %s.", request.getRequestURI(), client.getClientId()));

        validateRedirectUris(client);

        clientsService.addClientToInstitution(registryCode, client);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteClient(String registryCode, String clientId) {
        log.info(String.format("Incoming request: DELETE %s.", request.getRequestURI()));
        clientsService.deleteClient(registryCode, clientId);
        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<String> updateClient(String registryCode, String clientId, @Valid Client client) {
        log.info(String.format("Incoming request: PUT %s.", request.getRequestURI()));

        validateRedirectUris(client);

        clientsService.updateClient(registryCode, clientId, client);

        return ResponseEntity.ok().build();
    }

    private void validateRedirectUris(Client client) {
        if (adminConfigurationProvider.isSsoMode()) {
            validateUri(client.getBackchannelLogoutUri(), ERR_BACKCHANNEL_URI);
            List<String> postLogoutRedirectUris = client.getPostLogoutRedirectUris();
            if (postLogoutRedirectUris == null || postLogoutRedirectUris.isEmpty()) {
                throw new ValidationException(ERR_POST_LOGOUT_REDIRECT_URI);
            }
            postLogoutRedirectUris.forEach(uri -> validateUri(uri, ERR_POST_LOGOUT_REDIRECT_URI));
        }
        client.getRedirectUris().forEach(uri -> validateUri(uri, ERR_REDIRECT_URI));
    }

    private void validateUri(String uri, String errMsg) {
        try {
            URL url = new URL(uri);
            String protocol = url.getProtocol();
            String userInfo = url.getUserInfo();
            String fragment = url.toURI().getRawFragment();
            if (userInfo != null || !protocol.equals("https") || fragment != null)
                throw new ValidationException(errMsg);
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new ValidationException(errMsg);
        }
    }

    @Override
    public ResponseEntity<List<InstitutionMetainfo>> getInstitutionMetainfo() {
        log.info(String.format("Incoming request: GET %s.", request.getRequestURI()));
        return ResponseEntity.ok(institutionsService.getInstitutionsMetainfo());
    }

    @Override
    public ResponseEntity<List<Institution>> getAllInstitutions(String filterBy) {
        log.info(String.format("Incoming request: GET %s, with query: %s.", request.getRequestURI(), request.getQueryString()));
        return ResponseEntity.ok(institutionsService.getInstitutions(filterBy));
    }

    @Override
    public ResponseEntity<Void> addInstitution(Institution institution) {
        log.info(String.format("Incoming request: POST %s, with registry_code: %s.", request.getRequestURI(), institution.getRegistryCode()));

        institutionsService.addInstitution(institution);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> updateInstitution(String institutionId, Institution institution) {
        log.info(String.format("Incoming request: PUT %s.", request.getRequestURI()));

        institutionsService.updateInstitution(institution);

        return ResponseEntity.ok().build();
    }

    @Override
    public ResponseEntity<Void> deleteInstitution(String registryCode) {
        log.info(String.format("Incoming request: DELETE %s.", request.getRequestURI()));

        institutionsService.deleteInstitution(registryCode);

        return ResponseEntity.ok().build();
    }
}
