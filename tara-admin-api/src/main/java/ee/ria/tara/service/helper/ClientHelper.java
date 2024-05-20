package ee.ria.tara.service.helper;


import ee.ria.tara.controllers.exception.FatalApiException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.ClientContact;
import ee.ria.tara.model.ClientMidSettings;
import ee.ria.tara.model.ClientSmartIdSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.model.NameTranslations;
import ee.ria.tara.model.ShortNameTranslations;
import ee.ria.tara.service.model.HydraClient;
import ee.ria.tara.service.model.HydraClientMetadata;
import ee.ria.tara.service.model.HydraOidcClientInstitution;
import ee.ria.tara.service.model.OidcClient;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.bouncycastle.util.encoders.Hex;

import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.stream.Collectors;

@Slf4j
public class ClientHelper {

    private static final String ACCESS_TOKEN_STRATEGY_JWT = "jwt";
    public static final String SCOPE_REPRESENTEE = "representee.*";
    public static final String SCOPE_REPRESENTEE_LIST = "representee_list";

    public static Client convertToClient(HydraClient hydraClient, ee.ria.tara.repository.model.Client entity) {
        Client client = convertToClient(hydraClient);

        if (entity != null) {
            client.getInstitutionMetainfo().setRegistryCode(entity.getInstitution().getRegistryCode());
            client.getInstitutionMetainfo().setName(entity.getInstitution().getName());

            client.setId(entity.getId().toString());
            client.setEidasRequesterId(entity.getEidasRequesterId());
            client.setDescription(entity.getDescription());
            client.setInfoNotificationEmails(entity.getInfoNotificationEmails());
            client.setSlaNotificationEmails(entity.getSlaNotificationEmails());
            client.setClientContacts(getClientContacts(entity));
            client.setTokenRequestAllowedIpAddresses(entity.getTokenRequestAllowedIpAddresses());

            if (!backchannelLogoutHostAndPortMatches(client.getBackchannelLogoutUri(),
                    entity.getBackchannelLogoutHostAndPort())) {
                log.warn("Client with ID '{}' has non-matching back-channel logout URL host and port in Hydra '{}' " +
                        "with value in admin database '{}', returning empty URL",
                        client.getClientId(),
                        client.getBackchannelLogoutUri(),
                        entity.getBackchannelLogoutHostAndPort());
                client.setBackchannelLogoutUri(null);
            }

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

    private static boolean backchannelLogoutHostAndPortMatches(String backchannelLogoutUri,
                                                               String backchannelLogoutHostAndPort) {
        String hostAndPortFromParsedUri;
        try {
            hostAndPortFromParsedUri = getBackchannelLogoutHostAndPort(backchannelLogoutUri);
        } catch (MalformedURLException e) {
            return false;
        }
        return StringUtils.equals(hostAndPortFromParsedUri, backchannelLogoutHostAndPort);
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
        client.setClientLogo(hydraClient.getMetadata().getOidcClient().getLogo());
        client.setEidasRequesterId(hydraClient.getMetadata().getOidcClient().getEidasRequesterId());
        client.setSkipUserConsentClientIds(hydraClient.getMetadata().getSkipUserConsentClientIds());
        client.setTokenEndpointAuthMethod(Client.TokenEndpointAuthMethodEnum.fromValue(hydraClient.getTokenEndpointAuthMethod()));
        client.setIsUserConsentRequired(hydraClient.getMetadata().getDisplayUserConsent());
        client.setClientUrl(getOidcClientLegacyReturnUrl(hydraClient));
        client.setSmartidSettings(getSmartidSettings(hydraClient));
        client.setMidSettings(getMobileIdSettings(hydraClient));
        client.setCreatedAt(OffsetDateTime.parse(hydraClient.getCreatedAt()));
        client.setUpdatedAt(OffsetDateTime.parse(hydraClient.getUpdatedAt()));
        if (hydraClient.getAccessTokenStrategy() != null && hydraClient.getAccessTokenStrategy().equals(ACCESS_TOKEN_STRATEGY_JWT)) {
            client.setAccessTokenJwtEnabled(true);
        }
        client.setAccessTokenAudienceUris(hydraClient.getAudience());
        client.setAccessTokenLifespan(hydraClient.getAuthorizationCodeGrantAccessTokenLifespan());
        client.setPaasukeParameters(hydraClient.getMetadata().getPaasukeParameters());

        return client;
    }

    public static HydraClient convertToHydraClient(Client client, boolean ssoMode) {
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
        oidcClient.setEidasRequesterId(client.getEidasRequesterId());
        oidcClient.setInstitution(hydraOidcClientInstitution);
        oidcClient.setLogo(client.getClientLogo());

        metadata.setDisplayUserConsent(client.getIsUserConsentRequired());
        metadata.setOidcClient(oidcClient);
        metadata.setSkipUserConsentClientIds(client.getSkipUserConsentClientIds() != null ? getDistinctSkipUserConsentClientIds(client) : null);
        metadata.setPaasukeParameters(client.getPaasukeParameters());

        if (ssoMode) {
            hydraClient.setGrantTypes(List.of("authorization_code", "refresh_token"));
        }
        if (client.getAccessTokenJwtEnabled()) {
            hydraClient.setAccessTokenStrategy(ACCESS_TOKEN_STRATEGY_JWT);
        }
        hydraClient.setAudience(client.getAccessTokenAudienceUris());
        hydraClient.setAuthorizationCodeGrantAccessTokenLifespan(client.getAccessTokenLifespan());
        hydraClient.setRefreshTokenGrantAccessTokenLifespan(client.getAccessTokenLifespan());
        hydraClient.setClientId(client.getClientId());
        // NB! For backward compatibility with TARA-Server all client secrets must be saved to Ory Hydra as sha256 digests.
        hydraClient.setClientSecret(!ssoMode ? getDigest(client.getSecret()): client.getSecret());
        hydraClient.setClientName(client.getClientName() != null ? client.getClientName().getEt() : null);
        hydraClient.setScope(String.join(" ", client.getScope()));
        hydraClient.setRedirectUris(client.getRedirectUris());
        hydraClient.setPostLogoutRedirectUris(client.getPostLogoutRedirectUris());
        hydraClient.setMetadata(metadata);
        hydraClient.setBackchannelLogoutUri(client.getBackchannelLogoutUri());
        hydraClient.setTokenEndpointAuthMethod(client.getTokenEndpointAuthMethod().getValue());

        hydraOidcClientInstitution.setRegistryCode(client.getInstitutionMetainfo().getRegistryCode());
        hydraOidcClientInstitution.setSector(client.getInstitutionMetainfo().getType().getType().toString());

        return hydraClient;
    }

    @SneakyThrows
    public static ee.ria.tara.repository.model.Client convertToEntity(Client client, ee.ria.tara.repository.model.Institution institution) {
        ee.ria.tara.repository.model.Client entity = new ee.ria.tara.repository.model.Client();

        entity.setId(client.getId() == null ? null : Long.valueOf(client.getId()));
        entity.setClientId(client.getClientId());
        entity.setEidasRequesterId(client.getEidasRequesterId());
        entity.setDescription(client.getDescription());
        entity.setInstitution(institution);
        entity.setInfoNotificationEmails(client.getInfoNotificationEmails());
        entity.setSlaNotificationEmails(client.getSlaNotificationEmails());
        entity.setClientContacts(getClientContacts(client, entity));
        entity.setBackchannelLogoutHostAndPort(getBackchannelLogoutHostAndPort(client.getBackchannelLogoutUri()));
        entity.setTokenRequestAllowedIpAddresses(client.getTokenRequestAllowedIpAddresses());

        return entity;
    }

    private static List<ee.ria.tara.repository.model.ClientContact> getClientContacts(Client client, ee.ria.tara.repository.model.Client parentEntity) {
        if (client.getClientContacts() == null)
            return List.of();

        return client.getClientContacts()
                .stream()
                .map(model -> toEntity(parentEntity, model))
                .collect(Collectors.toList());
    }

    private static ee.ria.tara.repository.model.ClientContact toEntity(ee.ria.tara.repository.model.Client parentEntity,
                                                                       ClientContact model) {
        ee.ria.tara.repository.model.ClientContact clientContact = new ee.ria.tara.repository.model.ClientContact();
        clientContact.setClient(parentEntity);
        clientContact.setName(model.getName());
        clientContact.setEmail(model.getEmail());
        clientContact.setPhone(model.getPhone());
        clientContact.setDepartment(model.getDepartment());
        return clientContact;
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

    private static List<String> getDistinctSkipUserConsentClientIds(Client client) {
        return client.getSkipUserConsentClientIds().stream()
                .distinct()
                .collect(Collectors.toList());
    }

    @SneakyThrows
    public static String getDigest(String secret) {
        if (secret == null)
            return null;

        try {
            return Hex.toHexString(MessageDigest.getInstance("SHA-256").digest(secret.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            log.error("Failed to retrieve SHA-256 algorithm.", e);
            throw new FatalApiException(e);
        }
    }

    private static String getBackchannelLogoutHostAndPort(String backchannelLogoutUri) throws MalformedURLException {
        if (StringUtils.isNotBlank(backchannelLogoutUri)) {
            URL url = new URL(backchannelLogoutUri);
            int port = url.getPort();
            if (port < 0) {
                port = 443;
            }
            return url.getHost().toLowerCase(Locale.ROOT) + ":" + port;
        }
        return null;
    }

}
