package ee.ria.tara.controllers;

import ee.ria.tara.api.ClientsApi;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientImportResponse;
import ee.ria.tara.model.Institution;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.ImportService;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.Assert;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ClientsController implements ClientsApi {
    private final ClientsService service;
    private final ImportService importService;

    @SneakyThrows
    @Override
    public ResponseEntity<List<Client>> getAllClients(@Valid String filterBy) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(String.format("Incoming request: GET %s, with query %s.", request.getRequestURI(), request.getQueryString()));
        return ResponseEntity.ok(service.getAllClients(filterBy));
    }

    @Override
    public ResponseEntity<ClientImportResponse> importClients(@RequestPart("file") MultipartFile fileName) {
        try {
            log.info("Upload file: " + fileName.getOriginalFilename());
            Map<Institution, List<Client>> clients = importService.importFromExcelFile(fileName.getInputStream());
            ClientImportResponse result = saveClients(clients);
            log.info(String.format("Client import result: %s", result));
            return ResponseEntity.ok(result);
        } catch (IllegalArgumentException e) {
            log.error("Not a valid excel file: " + e.getMessage(), e);
            throw new ResponseStatusException(
                    HttpStatus.BAD_REQUEST, "Not a valid excel file", e);
        } catch (Exception e) {
            log.error("Error " + e.getMessage(), e);
            return ResponseEntity.status(500).build();
        }
    }

    private ClientImportResponse saveClients(Map<Institution, List<Client>> insitutionClients) throws Exception {
        List<Client> clientsFailedToMigrate = new ArrayList<>();
        int clientsTotal = 0;
        int clientsImportedOk = 0;

        for (Institution institution : insitutionClients.keySet()) {
            for (Client client : insitutionClients.get(institution)) {
                Assert.notNull(client.getInstitutionMetainfo(), "Missing institutionMetaInfo");
                Assert.notNull(client.getInstitutionMetainfo().getRegistryCode(), "Missing institutionMetaInfo.registryCode");
                if (saveClient(institution, client))
                    clientsImportedOk++;
                else
                    clientsFailedToMigrate.add(client);

                clientsTotal++;
            }
        }

        ClientImportResponse response = new ClientImportResponse();
        response.setStatus(clientsFailedToMigrate.size() == 0 ? "FINISHED_SUCCESSFULLY" : "FINISHED_WITH_ERRORS");
        response.setClientsCount(clientsTotal);
        response.setClientsImportSuccessCount(clientsImportedOk);
        response.setClientsImportFailedCount(clientsFailedToMigrate.size());
        response.setClientsNotImported(clientsFailedToMigrate.stream().map(Client::getClientId).collect(Collectors.toList()));
        return response;
    }

    private boolean saveClient(ee.ria.tara.model.Institution institution, Client client) {
        try {
            importService.saveClient(institution, client);
            log.info(String.format("TARA client: %s successfully imported", client.getClientId()));
            return true;
        } catch (Exception e) {
            log.error("Failed to import client: " + client.getClientId(), e);
            return false;
        }
    }
}
