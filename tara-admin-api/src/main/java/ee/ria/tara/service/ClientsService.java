package ee.ria.tara.service;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
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
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientHelper.convertToClient;
import static ee.ria.tara.service.helper.ClientHelper.convertToHydraClient;
import static java.util.function.Function.identity;

@Slf4j
@Service
@RequiredArgsConstructor
public class ClientsService {

    static final int SIGNING_SECRET_LENGTH = 32;

    private final ClientRepository clientRepository;
    private final InstitutionRepository institutionRepository;
    private final ClientSecretEmailService clientSecretEmailService;
    private final OidcService oidcService;
    private final AdminConfigurationProvider adminConfigurationProvider;
    private final ClientValidator clientValidator;
    private final ScopeFilter scopeFilter;
    private final SecureRandomAlphaNumericStringGenerator secureRandomAlphaNumericStringGenerator;

    public Client getClient(@NonNull String clientId) throws FatalApiException {
        HydraClient hydraClient = this.oidcService.getClient(clientId);
        ee.ria.tara.repository.model.Client entity = clientRepository.findByClientId(clientId);
        return convertToClient(hydraClient, entity);
    }

    public List<Client> getAllClients() throws FatalApiException {
        List<HydraClient> hydraClients = this.oidcService.getAllClients();

        List<Client> clients = new ArrayList<>();
        Map<String, ee.ria.tara.repository.model.Client> clientMap = clientRepository.findAll()
                .stream()
                .collect(Collectors.toMap(entity -> entity.getClientId(), identity()));

        hydraClients.forEach(hydraClient ->
                clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

        return clients;
    }

    public Map<String, List<String>> getAllClientsFromClientRepository() {
            List<ee.ria.tara.repository.model.Client> clientList = new ArrayList<>(clientRepository.findAll());
            Map<String, List<String>> clientsIps = clientList
                .stream()
                .collect(Collectors.toMap(client -> client.getClientId(), client -> client.getTokenRequestAllowedIpAddresses()));

            return clientsIps;
    }

    public List<Client> getClientsByInstitution(String registryCode) throws ApiException {
        Map<String, ee.ria.tara.repository.model.Client> clientMap;
        List<Client> clients = new ArrayList<>();

        clientMap = clientRepository.findAllByInstitution_RegistryCode(registryCode)
                .stream()
                .collect(Collectors.toMap(entity -> entity.getClientId(), identity()));

        oidcService.getAllClients()
                .stream()
                .filter(hydraClient -> clientMap.containsKey(hydraClient.getClientId()))
                .forEach(hydraClient -> clients.add(convertToClient(hydraClient, clientMap.get(hydraClient.getClientId()))));

        return clients;
    }

    @Transactional(rollbackFor = Exception.class)
    public void addClientToInstitution(String registryCode, Client client) throws ApiException {
        this.saveClient(client, registryCode, null);

        log.info(String.format("Added client with client_id %s.", client.getClientId()));
    }

    @Transactional(rollbackFor = Exception.class)
    public void updateClient(String registryCode, String clientId, Client client) throws ApiException {
        this.saveClient(client, registryCode, clientId);

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
    //TODO: checking if `clientId` is null is not the correct way to check if we are creating a new client or updating
    // an existing one. We are also not checking if `clientId` equals `client.getClientId()`, a mismatch there could
    // lead to odd behaviour.
    private void saveClient(Client client, String registryCode, String clientId) {
        Institution institution = institutionRepository.findInstitutionByRegistryCode(registryCode);
        client.setScope(scopeFilter.filterInstitutionClientScopes(client.getScope(), institution.getType()));
        clientValidator.validateClient(client, institution.getType());
        saveClientEntity(ClientHelper.convertToEntity(client, institution));

        boolean ssoMode = adminConfigurationProvider.isSsoMode();
        HydraClient hydraClient = convertToHydraClient(client, ssoMode);
        if (clientId == null) {
            oidcService.createClient(hydraClient);
        } else {
            oidcService.updateClient(clientId, hydraClient);
        }

        if (shouldGenerateNewSecret(client)) {
            resetSecret(client.getClientId(), client, hydraClient, ssoMode);
        }
    }

    private void resetSecret(String clientId, Client client, HydraClient hydraClient, boolean ssoMode) {
        assertValidIdCode(client.getClientSecretExportSettings());
        String newSecret = secureRandomAlphaNumericStringGenerator.generate(SIGNING_SECRET_LENGTH);
        client.setSecret(newSecret);
        clientSecretEmailService.sendSigningSecretByEmail(client);

        hydraClient.setClientSecret(!ssoMode ? ClientHelper.getDigest(newSecret) : newSecret);
        oidcService.updateClient(clientId, hydraClient);
    }

    private void assertValidIdCode(ClientSecretExportSettings clientSecretExportSettings) throws InvalidDataException {
        if (!NationalIdCodeValidator.isValid(clientSecretExportSettings.getRecipientIdCode())) {
            log.error(String.format("Given CDOC national id code is not valid: %s.", clientSecretExportSettings.getRecipientIdCode()));
            throw new InvalidDataException("Client.secret.invalidIdCode");
        }
    }
}
