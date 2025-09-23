package ee.ria.tara.controllers;

import ee.ria.tara.api.ClientsApi;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientImportResponse;
import ee.ria.tara.service.ClientsService;
import ee.ria.tara.service.ImportService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@RestController
public class ClientsController implements ClientsApi {
    private final ClientsService service;
    private final ImportService importService;

    @SneakyThrows
    @Override
    public ResponseEntity<List<Client>> getAllClients(@Valid String clientId) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(String.format("Incoming request: GET %s, with query %s.", request.getRequestURI(), request.getQueryString()));
        return clientId != null ?
                ResponseEntity.ok(List.of(service.getClient(clientId))) :
                ResponseEntity.ok(service.getAllClients());
    }

    @Override
    public ResponseEntity<Map<String, List<String>>> getAllTokenRequestAllowedIpAddresses() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        log.info(String.format("Incoming request: GET %s, with query %s.", request.getRequestURI(), request.getQueryString()));
        Map<String, List<String>> clientsIps = service.getAllClientsFromClientRepository();
        return ResponseEntity.ok(clientsIps);
    }

    @Override
    public ResponseEntity<ClientImportResponse> importClients(@RequestPart("file") MultipartFile fileName) {
        try {
            log.info("Upload file: " + fileName.getOriginalFilename());
            ClientImportResponse result = importService.importFromExcelFile(fileName.getInputStream());
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

}
