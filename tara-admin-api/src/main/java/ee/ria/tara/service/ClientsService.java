package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.configuration.providers.TaraOidcConfigurationProvider;
import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.repository.model.Institution;
import ee.ria.tara.service.helper.ClientHelper;
import ee.ria.tara.service.helper.ClientValidator;
import ee.ria.tara.service.helper.NationalIdCodeValidator;
import ee.ria.tara.service.helper.ScopeFilter;
import ee.ria.tara.service.helper.SecureRandomAlphaNumericStringGenerator;
import ee.ria.tara.service.model.HydraClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientHelper.convertToClient;
import static ee.ria.tara.service.helper.ClientHelper.convertToHydraClient;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsService {
    private static final int SIGNING_SECRET_LENGTH = 32;

    private final ClientRepository clientRepository;
    private final InstitutionRepository institutionRepository;
    private final ClientSecretEmailService clientSecretEmailService;
    private final OidcService oidcService;
    private final AdminConfigurationProvider adminConfigurationProvider;
    private final ClientValidator clientValidator;
    private final ScopeFilter scopeFilter;
    private final TaraOidcConfigurationProvider taraOidcConfigurationProvider;

    public List<Client> getAllClients(String clientId) throws FatalApiException {
        List<HydraClient> hydraClients = this.oidcService.getAllClients(clientId);

        if (clientId != null) {
            ee.ria.tara.repository.model.Client entity = clientRepository.findByClientId(clientId);

            return hydraClients.stream()
                    .map(hydraClient -> convertToClient(hydraClient, entity))
                    .collect(Collectors.toList());
        } else {
            List<Client> clients = new ArrayList<>();
            Map<String, ee.ria.tara.repository.model.Client> clientMap = clientRepository.findAll()
                    .stream()
                    .collect(Collectors.toMap(entity -> entity.getClientId(), entity -> entity));

            hydraClients.forEach(hydraClient ->
                    clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

            return clients;
        }
    }

    public Map<String, List<String>> getAllClientsFromClientRepository() {
            List<ee.ria.tara.repository.model.Client> clientList = new ArrayList<>(clientRepository.findAll());
            Map<String, List<String>> clientsIps = clientList
                .stream()
                .collect(Collectors.toMap(ee.ria.tara.repository.model.Client::getClientId, ee.ria.tara.repository.model.Client::getTokenRequestAllowedIpAddresses));

            return clientsIps;
    }

    public List<Client> getAllInstitutionsClients(String registryCode) throws ApiException {
        Map<String, ee.ria.tara.repository.model.Client> clientMap;
        List<Client> clients = new ArrayList<>();

        clientMap = clientRepository.findAllByInstitution_RegistryCode(registryCode)
                .stream()
                .collect(Collectors.toMap(entity -> entity.getClientId(), entity -> entity));

        oidcService.getAllClients(null)
                .stream()
                .filter(hydraClient -> clientMap.containsKey(hydraClient.getClientId()))
                .forEach(hydraClient -> clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

        return clients;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addClientToInstitution(String registryCode, Client client) throws ApiException {
        String uri = String.format("%s/clients", taraOidcConfigurationProvider.getUrl());
        this.saveClient(client, registryCode, uri, HttpMethod.POST);

        log.info(String.format("Added client with client_id %s.", client.getClientId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateClient(String registryCode, String clientId, Client client) throws ApiException {
        String uri = String.format("%s/clients/%s", taraOidcConfigurationProvider.getUrl(), clientId);
        this.saveClient(client, registryCode, uri, HttpMethod.PUT);

        log.info(String.format("Updated client with client_id %s.", client.getClientId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void deleteClient(String registryCode, String clientId) throws ApiException {
        this.clientRepository.deleteByClientIdAndInstitution_RegistryCode(clientId, registryCode);

        oidcService.deleteClient(clientId);
    }

    private void saveClientEntity(ee.ria.tara.repository.model.Client client) {
        try {
            log.info("Saving client: " + client);
            clientRepository.save(client);
        } catch (DataIntegrityViolationException ex) {
            log.error(String.format("Failed to save client: %s.", client.getClientId()), ex);
            throw new InvalidDataException("Client.exists");
        }
    }

    private boolean shouldGenerateNewSecret(Client client) {
        ClientSecretExportSettings settings = client.getClientSecretExportSettings();
        return settings != null && settings.getRecipientEmail() != null && settings.getRecipientIdCode() != null;
    }

    /*
      1. Save client to TARA database, which is reversible transaction.
      2. Save client to Hydra.
      3. If requested, generate new secret and send e-mail.
      4. Save client to Hydra again, now with newly generated secret.

      This ensures client is saved to Hydra with old secret even when e-mail sending fails.
      And no premature e-mails will be sent if saving client to Hydra fails due to invalid
      values other than the secret.
     */
    private void saveClient(Client client, String registryCode, String uri, HttpMethod httpMethod) {
        Institution institution = institutionRepository.findInstitutionByRegistryCode(registryCode);
        client.setScope(scopeFilter.filterInstitutionClientScopes(client.getScope(), institution.getType()));
        clientValidator.validateClient(client, institution.getType());
        saveClientEntity(ClientHelper.convertToEntity(client, institution));

        boolean ssoMode = adminConfigurationProvider.isSsoMode();
        HydraClient hydraClient = convertToHydraClient(client, ssoMode);
        oidcService.saveClient(hydraClient, uri, httpMethod);

        if (shouldGenerateNewSecret(client)) {
            assertValidIdCode(client.getClientSecretExportSettings());
            String newSecret = SecureRandomAlphaNumericStringGenerator.INSTANCE.generate(SIGNING_SECRET_LENGTH);
            client.setSecret(newSecret);
            clientSecretEmailService.sendSigningSecretByEmail(client);

            hydraClient.setClientSecret(!ssoMode ? ClientHelper.getDigest(newSecret) : newSecret);
            String putRequestUri = String.format("%s/clients/%s", taraOidcConfigurationProvider.getUrl(), client.getClientId());
            oidcService.saveClient(hydraClient, putRequestUri, HttpMethod.PUT);
        }
    }

    private void assertValidIdCode(ClientSecretExportSettings clientSecretExportSettings) throws InvalidDataException {
        if (!NationalIdCodeValidator.isValid(clientSecretExportSettings.getRecipientIdCode())) {
            log.error(String.format("Given CDOC national id code is not valid: %s.", clientSecretExportSettings.getRecipientIdCode()));
            throw new InvalidDataException("Client.secret.invalidIdCode");
        }
    }
}
