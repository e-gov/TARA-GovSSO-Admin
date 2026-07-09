package ee.ria.tara.mapper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.Client;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.OffsetDateTime;

import static ee.ria.tara.service.helper.ClientTestHelper.validSSOClient;
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

    private HydraClient toHydraClientWithTimestamps(Client client) {
        HydraClient hydraClient = clientMapper.toHydraClient(client);
        hydraClient.setCreatedAt(OffsetDateTime.now().toString());
        hydraClient.setUpdatedAt(OffsetDateTime.now().toString());
        return hydraClient;
    }
}
