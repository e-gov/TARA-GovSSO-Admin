package ee.ria.tara.service.helper;

import ee.ria.tara.model.Client;
import ee.ria.tara.model.Client.TokenEndpointAuthMethodEnum;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSecretExportSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.service.model.HydraClient;
import ee.ria.tara.service.model.OidcClient;

import java.time.OffsetDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClientTestHelper {
    public static void compareClientWithHydraClient(HydraClient expected, Client actual) {
        OidcClient expectedOidcClient = expected.getMetadata().getOidcClient();

        assertEquals(expected.getClientId(), actual.getClientId());
        assertEquals(expectedOidcClient.getLegacyReturnUrl(), actual.getClientUrl());
        assertEquals(expected.getRedirectUris(), actual.getRedirectUris());
        assertEquals(expected.getScope(), String.join(" ", actual.getScope()));
        assertEquals(expected.getMetadata().getDisplayUserConsent(), actual.getIsUserConsentRequired());

        assertEquals(expected.getClientName(), actual.getClientName().getEt());
        assertEquals(expectedOidcClient.getNameTranslations().getEt(), actual.getClientName().getEt());
        assertEquals(expectedOidcClient.getNameTranslations().getEn(), actual.getClientName().getEn());
        assertEquals(expectedOidcClient.getNameTranslations().getRu(), actual.getClientName().getRu());

        assertEquals(expectedOidcClient.getShortName(), actual.getClientShortName().getEt());
        assertEquals(expectedOidcClient.getShortNameTranslations().getEt(), actual.getClientShortName().getEt());
        assertEquals(expectedOidcClient.getShortNameTranslations().getEn(), actual.getClientShortName().getEn());
        assertEquals(expectedOidcClient.getShortNameTranslations().getRu(), actual.getClientShortName().getRu());

        assertEquals(OffsetDateTime.parse(expected.getCreatedAt()), actual.getCreatedAt());
        assertEquals(OffsetDateTime.parse(expected.getUpdatedAt()), actual.getUpdatedAt());
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
        return validTARAClient(1);
    }

    public static Client validTARAClient(int id) {
        Client client = new Client();
        NameTranslations nameTranslations = new NameTranslations();
        ShortNameTranslations shortNameTranslations = new ShortNameTranslations();
        InstitutionType institutionType = new InstitutionType();
        InstitutionMetainfo institutionMetainfo = new InstitutionMetainfo();
        ClientSecretExportSettings clientSecretExportSettings = new ClientSecretExportSettings();

        institutionType.setType(InstitutionType.TypeEnum.PUBLIC);

        institutionMetainfo.setType(institutionType);
        institutionMetainfo.setRegistryCode("12345678");
        institutionMetainfo.setName("Example Institution");

        nameTranslations.setEt("Nimi");
        shortNameTranslations.setEt("Nimi");

        clientSecretExportSettings.setRecipientIdCode("10101010005");
        clientSecretExportSettings.setRecipientEmail("email@not.real.localhost");

        client.setClientId("ClientID-" + id);
        client.setClientName(nameTranslations);
        client.setClientShortName(shortNameTranslations);
        client.setClientUrl("https://localhost:4200");
        client.setInstitutionMetainfo(institutionMetainfo);
        client.setRedirectUris(List.of("https://localhost:4200"));
        client.setTokenRequestAllowedIpAddresses(List.of("1.1.1.1", "1.1.1.10"));
        client.setAccessTokenJwtEnabled(false);
        client.setAccessTokenAudienceUris(null);
        client.setBackchannelLogoutUri(null);
        client.setPostLogoutRedirectUris(null);
        client.setMinimumAcrValue(Client.MinimumAcrValueEnum.HIGH);
        client.setClientSecretExportSettings(clientSecretExportSettings);
        client.setScope(List.of("openid"));
        client.setEidasRequesterId("urn:uuid:f75256ee-740d-4427-84ad-0f4b08417259");
        client.setSmartidSettings(new ClientSmartIdSettings());
        client.setMidSettings(new ClientMidSettings());
        client.setCreatedAt(OffsetDateTime.now());
        client.setUpdatedAt(OffsetDateTime.now());
        client.setClientLogo(null);
        client.setTokenEndpointAuthMethod(TokenEndpointAuthMethodEnum.CLIENT_SECRET_BASIC);

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
