package ee.ria.tara.service.helper;


import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.model.*;
import ee.ria.tara.service.model.HydraClient;
import ee.ria.tara.service.model.HydraClientMetadata;
import ee.ria.tara.service.model.HydraOidcClientInstitution;
import ee.ria.tara.service.model.OidcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bouncycastle.util.encoders.Hex;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Slf4j
public class ClientHelper {

    public static Client convertToClient(HydraClient hydraClient, ee.ria.tara.repository.model.Client entity) {
        Client client = convertToClient(hydraClient);

        if (entity != null) {
            client.getInstitutionMetainfo().setRegistryCode(entity.getInstitution().getRegistryCode());
            client.getInstitutionMetainfo().setName(entity.getInstitution().getName());

            client.setId(entity.getId().toString());
            client.setDescription(entity.getDescription());
            client.setInfoNotificationEmails(entity.getInfoNotificationEmails());
            client.setSlaNotificationEmails(entity.getSlaNotificationEmails());
            client.setClientContacts(getClientContacts(entity));
            client.setClientLogo(entity.getClientLogo());

        } else {
            if (log.isDebugEnabled()) {
                log.warn(String.format("Client with client_id: %s  not found in database.", hydraClient.getClientId()));
            }
        }

        return client;
    }

    private static List<ClientContact> getClientContacts(ee.ria.tara.repository.model.Client entity) {
        List<ClientContact> clientContacts = new ArrayList<>();
        for (ee.ria.tara.repository.model.ClientContact contact : entity.getClientContacts()) {
            ClientContact clientContact = new ClientContact();
            clientContact.setName(contact.getName());
            clientContact.setEmail(contact.getEmail());
            clientContact.setPhone(contact.getPhone());
            clientContact.setDepartment(contact.getDepartment());
            clientContacts.add(clientContact);
        }
        return clientContacts;
    }

    public static Client convertToClient(HydraClient hydraClient) {
        Client client = new Client();
        InstitutionType institutionType = new InstitutionType();
        InstitutionMetainfo institutionMetainfo = new InstitutionMetainfo();

        institutionType.setType(InstitutionType.TypeEnum.fromValue(hydraClient.getMetadata().getOidcClient().getInstitution().getSector()));
        institutionMetainfo.setType(institutionType);

        client.setClientId(hydraClient.getClientId());
        client.setInstitutionMetainfo(institutionMetainfo);
        client.setClientName(getNameTranslations(hydraClient.getMetadata().getOidcClient().getNameTranslations()));
        client.setClientShortName(getShortNameTranslations(hydraClient.getMetadata().getOidcClient().getShortNameTranslations()));
        client.setRedirectUris(hydraClient.getRedirectUris());
        client.setPostLogoutRedirectUris(hydraClient.getPostLogoutRedirectUris());
        client.setScope(Arrays.asList(hydraClient.getScope().split(" ")));
        client.setBackchannelLogoutUri(hydraClient.getBackchannelLogoutUri());

        client.setIsUserConsentRequired(hydraClient.getMetadata().getDisplayUserConsent());
        client.setClientUrl(getOidcClientLegacyReturnUrl(hydraClient));
        client.setSmartidSettings(getSmartidSettings(hydraClient));
        client.setMidSettings(getMobileIdSettings(hydraClient));
        client.setCreatedAt(OffsetDateTime.parse(hydraClient.getCreatedAt()));
        client.setUpdatedAt(OffsetDateTime.parse(hydraClient.getUpdatedAt()));

        return client;
    }

    public static HydraClient convertToHydraClient(Client client, boolean hashSecret) {
        HydraClientMetadata metadata = new HydraClientMetadata();
        OidcClient oidcClient = new OidcClient();
        HydraClient hydraClient = new HydraClient();
        HydraOidcClientInstitution hydraOidcClientInstitution = new HydraOidcClientInstitution();

        oidcClient.setMidSettings(client.getMidSettings());
        oidcClient.setSmartidSettings(client.getSmartidSettings());
        oidcClient.setName(client.getClientName() != null ? client.getClientName().getEt() : null);
        oidcClient.setNameTranslations(client.getClientName());
        oidcClient.setShortName(client.getClientShortName() != null ? client.getClientShortName().getEt() : null);
        oidcClient.setShortNameTranslations(client.getClientShortName());
        oidcClient.setLegacyReturnUrl(client.getClientUrl());
        oidcClient.setInstitution(hydraOidcClientInstitution);

        metadata.setDisplayUserConsent(client.getIsUserConsentRequired());
        metadata.setOidcClient(oidcClient);

        hydraClient.setClientId(client.getClientId());
        // NB! For backward compatibility with TARA-Server all client secrets must be saved to Ory Hydra as sha256 digests.
        hydraClient.setClientSecret(hashSecret ? getDigest(client.getSecret()): client.getSecret());
        hydraClient.setClientName(client.getClientName() != null ? client.getClientName().getEt() : null);

        hydraClient.setScope(String.join(" ", client.getScope()));

        hydraClient.setRedirectUris(client.getRedirectUris());
        hydraClient.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris());
        hydraClient.setMetadata(metadata);

        hydraClient.setBackchannelLogoutUri(client.getBackchannelLogoutUri());

        hydraOidcClientInstitution.setRegistryCode(client.getInstitutionMetainfo().getRegistryCode());
        hydraOidcClientInstitution.setSector(client.getInstitutionMetainfo().getType().getType().toString());

        return hydraClient;
    }

    public static ee.ria.tara.repository.model.Client convertToEntity(Client client, ee.ria.tara.repository.model.Institution institution) {
        ee.ria.tara.repository.model.Client entity = new ee.ria.tara.repository.model.Client();

        entity.setId(client.getId() == null ? null : Long.valueOf(client.getId()));
        entity.setClientId(client.getClientId());
        entity.setDescription(client.getDescription());
        entity.setInstitution(institution);
        entity.setInfoNotificationEmails(client.getInfoNotificationEmails());
        entity.setSlaNotificationEmails(client.getSlaNotificationEmails());
        entity.setClientContacts(getClientContacts(client, entity));
        entity.setClientLogo(client.getClientLogo());
        return entity;
    }

    private static List<ee.ria.tara.repository.model.ClientContact> getClientContacts(Client client, ee.ria.tara.repository.model.Client parentEntity) {
        if (client.getClientContacts() == null)
            return null;

        List<ee.ria.tara.repository.model.ClientContact> list = new ArrayList<>();
        for (ClientContact model : client.getClientContacts()) {
            ee.ria.tara.repository.model.ClientContact clientContact = new ee.ria.tara.repository.model.ClientContact();
            clientContact.setClient(parentEntity);
            clientContact.setName(model.getName());
            clientContact.setEmail(model.getEmail());
            clientContact.setPhone(model.getPhone());
            clientContact.setDepartment(model.getDepartment());
            list.add(clientContact);
        }
        return list;
    }

    private static NameTranslations getNameTranslations(NameTranslations nameTranslations) {
        NameTranslations translations = new NameTranslations();

        if(nameTranslations != null) {
            translations.setEt(nameTranslations.getEt());
            translations.setEn(nameTranslations.getEn());
            translations.setRu(nameTranslations.getRu());
        }

        return translations;
    }

    private static ShortNameTranslations getShortNameTranslations(ShortNameTranslations shortNameTranslations) {
        ShortNameTranslations translations = new ShortNameTranslations();

        if(shortNameTranslations != null) {
            translations.setEt(shortNameTranslations.getEt());
            translations.setEn(shortNameTranslations.getEn());
            translations.setRu(shortNameTranslations.getRu());
        }

        return translations;
    }

    private static ClientSmartIdSettings getSmartidSettings(HydraClient hydraClient) {
        ClientSmartIdSettings settings = new ClientSmartIdSettings();
        ClientSmartIdSettings hydraClientSmartIdSettings = hydraClient.getMetadata().getOidcClient().getSmartidSettings();

        if(hydraClientSmartIdSettings != null) {
            settings.setRelyingPartyName(hydraClientSmartIdSettings.getRelyingPartyName());
            settings.setRelyingPartyUUID(hydraClientSmartIdSettings.getRelyingPartyUUID());
            settings.setShouldUseAdditionalVerificationCodeCheck(hydraClientSmartIdSettings.getShouldUseAdditionalVerificationCodeCheck());
        }

        return settings;
    }

    private static ClientMidSettings getMobileIdSettings(HydraClient hydraClient) {
        ClientMidSettings settings = new ClientMidSettings();
        ClientMidSettings hydraClientMidSettings = hydraClient.getMetadata().getOidcClient().getMidSettings();

        if (hydraClientMidSettings != null) {
            settings.setRelyingPartyName(hydraClientMidSettings.getRelyingPartyName());
            settings.setRelyingPartyUUID(hydraClientMidSettings.getRelyingPartyUUID());
        }
        return settings;
    }

    private static String getOidcClientLegacyReturnUrl(HydraClient hydraClient) {
        return hydraClient.getMetadata().getOidcClient().getLegacyReturnUrl();
    }

    @SneakyThrows
    private static String getDigest(String secret) {
        if (secret == null)
            return null;

        try {
            return Hex.toHexString(MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to retrieve SHA-256 algorithm.", e);
            throw new FatalApiException(e);
        }
    }
}
