package ee.ria.tara.controllers;

import ee.ria.tara.api.InstitutionsApi;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.InstitutionsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
public class InstitutionsController implements InstitutionsApi {

    private final HttpServletRequest request;
    private final InstitutionsService institutionsService;
    private final ClientsService clientsService;

    @Override
    public ResponseEntity<List<Client>> getAllInstitutionClients(String registryCode) {
        log.info(String.format("Incoming request: GET %s.", request.getRequestURI()));
        return ResponseEntity.ok(clientsService.getAllInstitutionsClients(registryCode));
    }

    @Override
    public ResponseEntity<Void> addClientToInstitution(String registryCode, @Valid Client client) {
        log.info(String.format("Incoming request: POST %s, with client_id: %s.", request.getRequestURI(), client.getClientId()));

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

        clientsService.updateClient(registryCode, clientId, client);

        return ResponseEntity.ok().build();
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
