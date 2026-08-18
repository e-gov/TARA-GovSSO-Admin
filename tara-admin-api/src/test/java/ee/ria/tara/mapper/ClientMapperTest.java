package ee.ria.tara.mapper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static ee.ria.tara.service.helper.ClientTestHelper.validSSOClient;
import static ee.ria.tara.service.helper.ClientTestHelper.validSecuredAppSsoClient;
import static ee.ria.tara.service.helper.ClientTestHelper.validTARAClient;

class ClientMapperTest {

    private AdminConfigurationProvider adminConfigurationProvider;
    private ClientMapper clientMapper;

    @BeforeEach
    void setUp() {
        adminConfigurationProvider = new AdminConfigurationProvider();
        clientMapper = new ClientMapper(adminConfigurationProvider);
    }

    @Test
    void toModel_taraMode_clientTypeNotSet() {
        adminConfigurationProvider.setSsoMode(false);
        HydraClient hydraClient = toHydraClientWithTimestamps(validTARAClient());

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getClientType());
    }

    @Test
    void toModel_ssoMode_clientTypeMappedFromMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setClientType(Client.ClientTypeEnum.SECURED_APP);
        input.setSessionLifespan("30d");
        HydraClient hydraClient = toHydraClientWithTimestamps(input);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertEquals(Client.ClientTypeEnum.SECURED_APP, result.getClientType());
    }

    @Test
    void toModel_ssoModeMissingClientTypeInMetadata_clientTypeDefaulted() {
        adminConfigurationProvider.setSsoMode(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(validSSOClient());
        hydraClient.getMetadata().setClientType(null);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertEquals(Client.ClientTypeEnum.DEFAULT, result.getClientType());
    }

    @Test
    void toModel_taraMode_allowSecuredAppWebSessionNotSet() {
        adminConfigurationProvider.setSsoMode(false);
        HydraClient hydraClient = toHydraClientWithTimestamps(validTARAClient());
        hydraClient.getMetadata().setAllowSecuredAppWebSession(true);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getAllowSecuredAppWebSession());
    }

    @Test
    void toModel_ssoMode_allowSecuredAppWebSessionMappedFromMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setAllowSecuredAppWebSession(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(input);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertEquals(true, result.getAllowSecuredAppWebSession());
    }

    @Test
    void toModel_ssoModeMissingAllowSecuredAppWebSessionInMetadata_allowSecuredAppWebSessionNotSet() {
        adminConfigurationProvider.setSsoMode(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(validSSOClient());
        hydraClient.getMetadata().setAllowSecuredAppWebSession(null);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getAllowSecuredAppWebSession());
    }

    @Test
    void toModel_ssoModeSecuredAppClientType_allowSecuredAppWebSessionNotSet() {
        adminConfigurationProvider.setSsoMode(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(validSecuredAppSsoClient());
        hydraClient.getMetadata().setAllowSecuredAppWebSession(true);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getAllowSecuredAppWebSession());
    }

    @Test
    void toModel_ssoModeSecuredAppClientType_securedAppSessionMaxDurationNotSet() {
        adminConfigurationProvider.setSsoMode(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(validSecuredAppSsoClient());
        hydraClient.getMetadata().setSecuredAppSessionMaxDuration("12h");

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_taraMode_allowSecuredAppWebSessionNotSetInMetadata() {
        adminConfigurationProvider.setSsoMode(false);
        Client input = validTARAClient();
        input.setAllowSecuredAppWebSession(true);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toHydraClient_ssoModeDefaultClientType_allowSecuredAppWebSessionSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setAllowSecuredAppWebSession(true);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertEquals(true, result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toHydraClient_ssoModeDefaultClientTypeWithAllowSecuredAppWebSessionFalse_falseSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setAllowSecuredAppWebSession(false);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertEquals(false, result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toHydraClient_ssoModeDefaultClientTypeWithoutAllowSecuredAppWebSession_nullSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setAllowSecuredAppWebSession(null);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toHydraClient_ssoModeMissingClientType_allowSecuredAppWebSessionSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setClientType(null);
        input.setAllowSecuredAppWebSession(true);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertEquals(true, result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toHydraClient_ssoModeSecuredAppClientType_allowSecuredAppWebSessionNotSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSecuredAppSsoClient();
        input.setAllowSecuredAppWebSession(true);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getAllowSecuredAppWebSession());
    }

    @Test
    void toModel_taraMode_securedAppSessionMaxDurationNotSet() {
        adminConfigurationProvider.setSsoMode(false);
        HydraClient hydraClient = toHydraClientWithTimestamps(validTARAClient());
        hydraClient.getMetadata().setSecuredAppSessionMaxDuration("12h");

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getSecuredAppSessionMaxDuration());
    }

    @Test
    void toModel_ssoMode_securedAppSessionMaxDurationMappedFromMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setSecuredAppSessionMaxDuration("2d12h");
        HydraClient hydraClient = toHydraClientWithTimestamps(input);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertEquals("2d12h", result.getSecuredAppSessionMaxDuration());
    }

    @Test
    void toModel_ssoModeMissingSecuredAppSessionMaxDurationInMetadata_securedAppSessionMaxDurationNotSet() {
        adminConfigurationProvider.setSsoMode(true);
        HydraClient hydraClient = toHydraClientWithTimestamps(validSSOClient());
        hydraClient.getMetadata().setSecuredAppSessionMaxDuration(null);

        Client result = clientMapper.toModel(hydraClient, null);

        Assertions.assertNull(result.getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_taraMode_securedAppSessionMaxDurationNotSetInMetadata() {
        adminConfigurationProvider.setSsoMode(false);
        Client input = validTARAClient();
        input.setSecuredAppSessionMaxDuration("12h");

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_ssoModeDefaultClientType_securedAppSessionMaxDurationSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setSecuredAppSessionMaxDuration("2d12h");

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertEquals("60h", result.getMetadata().getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_ssoModeDefaultClientTypeWithoutSecuredAppSessionMaxDuration_nullSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setSecuredAppSessionMaxDuration(null);

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_ssoModeMissingClientType_securedAppSessionMaxDurationSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSSOClient();
        input.setClientType(null);
        input.setSecuredAppSessionMaxDuration("12h");

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertEquals("12h", result.getMetadata().getSecuredAppSessionMaxDuration());
    }

    @Test
    void toHydraClient_ssoModeSecuredAppClientType_securedAppSessionMaxDurationNotSetInMetadata() {
        adminConfigurationProvider.setSsoMode(true);
        Client input = validSecuredAppSsoClient();
        input.setSecuredAppSessionMaxDuration("12h");

        HydraClient result = clientMapper.toHydraClient(input);

        Assertions.assertNull(result.getMetadata().getSecuredAppSessionMaxDuration());
    }

    private HydraClient toHydraClientWithTimestamps(Client client) {
        HydraClient hydraClient = clientMapper.toHydraClient(client);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());
        return hydraClient;
    }
}
