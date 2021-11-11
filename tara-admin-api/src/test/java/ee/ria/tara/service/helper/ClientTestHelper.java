package ee.ria.tara.service.helper;

import ee.ria.tara.model.*;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.Assertions;

import java.time.OffsetDateTime;
import java.util.List;

public class ClientTestHelper {
    public static void compareClientWithHydraClient(Client client, HydraClient hydraClient){
        Assertions.assertEquals(client.getClientId(), hydraClient.getClientId());
        Assertions.assertEquals(client.getClientUrl(), hydraClient.getMetadata().getOidcClient().getLegacyReturnUrl());
        Assertions.assertEquals(client.getRedirectUris(), hydraClient.getRedirectUris());
        Assertions.assertEquals(String.join(" ", client.getScope()), hydraClient.getScope());
        Assertions.assertEquals(client.getIsUserConsentRequired(), hydraClient.getMetadata().getDisplayUserConsent());

        Assertions.assertEquals(client.getClientName().getEt(), hydraClient.getClientName());
        Assertions.assertEquals(client.getClientName().getEt(), hydraClient.getMetadata().getOidcClient().getNameTranslations().getEt());
        Assertions.assertEquals(client.getClientName().getEn(), hydraClient.getMetadata().getOidcClient().getNameTranslations().getEn());
        Assertions.assertEquals(client.getClientName().getRu(), hydraClient.getMetadata().getOidcClient().getNameTranslations().getRu());

        Assertions.assertEquals(client.getClientShortName().getEt(), hydraClient.getMetadata().getOidcClient().getShortName());
        Assertions.assertEquals(client.getClientShortName().getEt(), hydraClient.getMetadata().getOidcClient().getShortNameTranslations().getEt());
        Assertions.assertEquals(client.getClientShortName().getEn(), hydraClient.getMetadata().getOidcClient().getShortNameTranslations().getEn());
        Assertions.assertEquals(client.getClientShortName().getRu(), hydraClient.getMetadata().getOidcClient().getShortNameTranslations().getRu());

        Assertions.assertEquals(client.getCreatedAt(), OffsetDateTime.parse(hydraClient.getCreatedAt()));
        Assertions.assertEquals(client.getUpdatedAt(), OffsetDateTime.parse(hydraClient.getUpdatedAt()));
    }

    public static Client createTestClient() {
        Client client = new Client();
        NameTranslations nameTranslations = new NameTranslations();
        ShortNameTranslations shortNameTranslations = new ShortNameTranslations();
        InstitutionType institutionType = new InstitutionType();
        InstitutionMetainfo institutionMetainfo = new InstitutionMetainfo();

        institutionType.setType(InstitutionType.TypeEnum.PUBLIC);

        institutionMetainfo.setType(institutionType);
        institutionMetainfo.setRegistryCode("12345678");
        institutionMetainfo.setName("Example Institution");

        nameTranslations.setEt("Nimi");
        shortNameTranslations.setEt("Nimi");

        client.setClientId("ClientID");
        client.setClientName(nameTranslations);
        client.setClientShortName(shortNameTranslations);
        client.setClientUrl("https://localhost:4200");
        client.setInstitutionMetainfo(institutionMetainfo);
        client.setRedirectUris(List.of("https://localhost:4200"));
        client.setPostLogoutRedirectUris(List.of("https://localhost:4200"));
        ClientSecretExportSettings clientSecretExportSettings = new ClientSecretExportSettings();
        clientSecretExportSettings.setRecipientIdCode("10101010005");
        client.setClientSecretExportSettings(clientSecretExportSettings);
        client.setScope(List.of("mid"));
        client.setSmartidSettings(new ClientSmartIdSettings());
        client.setMidSettings(new ClientMidSettings());
        client.setCreatedAt(OffsetDateTime.now());
        client.setUpdatedAt(OffsetDateTime.now());

        return client;
    }

    public static Institution createTestInstitution(String registryCode, String name) {
        Institution institution = new Institution();
        institution.setRegistryCode(registryCode);
        institution.setName(name);
        return institution;
    }
}
