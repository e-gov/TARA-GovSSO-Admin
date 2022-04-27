package ee.ria.tara.service.helper;

import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.service.model.HydraClient;
import org.junit.jupiter.api.Assertions;

import java.time.OffsetDateTime;
import java.util.List;

public class ClientTestHelper {
    public static void compareClientWithHydraClient(Client client, HydraClient hydraClient) {
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

    public static Client validSSOClient() {
        Client client = validTARAClient();
        client.setEidasRequesterId(null);
        client.setBackchannelLogoutUri("https://localhost:4200");
        client.setPostLogoutRedirectUris(List.of("https://localhost:4200"));
        client.setClientLogo(new byte[10240]);
        return client;
    }

    public static Client validTARAClient() {
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
        client.setBackchannelLogoutUri(null);
        client.setPostLogoutRedirectUris(null);
        ClientSecretExportSettings clientSecretExportSettings = new ClientSecretExportSettings();
        clientSecretExportSettings.setRecipientIdCode("10101010005");
        client.setClientSecretExportSettings(clientSecretExportSettings);
        client.setScope(List.of("openid"));
        client.setEidasRequesterId("f75256ee-740d-4427-84ad-0f4b08417259");
        client.setSmartidSettings(new ClientSmartIdSettings());
        client.setMidSettings(new ClientMidSettings());
        client.setCreatedAt(OffsetDateTime.now());
        client.setUpdatedAt(OffsetDateTime.now());
        client.setClientLogo(null);

        return client;
    }

    public static Institution createValidPrivateInstitution(String registryCode, String name) {
        Institution institution = new Institution();
        institution.setRegistryCode(registryCode);
        institution.setName(name);
        InstitutionType institutionType = new InstitutionType();
        institutionType.setType(InstitutionType.TypeEnum.PRIVATE);
        institution.setType(institutionType);
        return institution;
    }
}
