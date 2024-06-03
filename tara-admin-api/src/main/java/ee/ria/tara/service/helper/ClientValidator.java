package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.ClientRepository;
import inet.ipaddr.AddressStringException;
import inet.ipaddr.IPAddressString;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.net.MalformedURLException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.Duration;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientHelper.SCOPE_REPRESENTEE;
import static ee.ria.tara.service.helper.ClientHelper.SCOPE_REPRESENTEE_LIST;

@Service
@RequiredArgsConstructor
public class ClientValidator {

    private static final int LOGO_ALLOWED_MAX_SIZE_IN_BYTES = 100 * 1024;
    private static final int MAX_SHORT_NAME_LENGTH = 40;
    private static final int MAX_SHORT_NAME_GSM7_LENGTH = 20;
    private static final String GSM_7_CHARACTERS = "@£$¥èéùìòÇØøÅåΔ_ΦΓΛΩΠΨΣΘΞ^{}[~]|€ÆæßÉ!\"#¤%&'()*+,-./0123456789:;<=>?¡ABCDEFGHIJKLMNOPQRSTUVWXYZÄÖÑÜ§¿abcdefghijklmnopqrstuvwxyzäöñüà \r\n\\";
    private static final String VALID_QUERY_PARAM_VALUE = "[\\w-%!:.~'()*]";
    private static final String VALID_PAASUKE_PARAMS_PATTERN = "^"+VALID_QUERY_PARAM_VALUE+"+(="+VALID_QUERY_PARAM_VALUE+"*)?(&"+VALID_QUERY_PARAM_VALUE+"+(="+VALID_QUERY_PARAM_VALUE+"*)?)*?$";
    private static final Duration MIN_ALLOWED_ACCESS_TOKEN_LIFESPAN = Duration.ofSeconds(1);

    private final AdminConfigurationProvider adminConfProvider;
    private final ClientRepository clientRepository;

    public void validateClient(Client client, InstitutionType.TypeEnum institutionType) {
        if (adminConfProvider.isSsoMode() && institutionType == InstitutionType.TypeEnum.PRIVATE) {
            throw new InvalidDataException("Client.sso.privateInstitution");
        }
        validateName(client);
        validateLogo(client.getClientLogo());
        validateIpAddresses(client.getTokenRequestAllowedIpAddresses());
        validateRedirectUris(client);
        validateScopes(client);
        if (client.getSkipUserConsentClientIds() != null) {
            validateSkipUserConsentClients(client.getSkipUserConsentClientIds(), client.getClientId());
        }
        validateEidasRequesterId(client);
        validatePaasukeQueryParameters(client);
        validateAccessTokenJwtEnabled(client);
        validateAccessTokenAudienceUris(client);
        validateAccessTokenLifespan(client);
    }

    private void validateIpAddresses(List<String> ipAddresses) {
        for (String ipAddress: ipAddresses) {
            if  (ipAddress.isBlank()) {
                throw new InvalidDataException("Client.tokenRequestAllowedIpAddresses.invalidIp");
            }
            try {
                new IPAddressString(ipAddress).validate();
            } catch (AddressStringException e) {
                throw new InvalidDataException("Client.tokenRequestAllowedIpAddresses.invalidIp");
            }
        }
    }

    private void validateSkipUserConsentClients(List<String> skipUserConsentClientIds, String currentClientId) {
        Set<String> existingClientIds = clientRepository.findAll()
                .stream()
                .map(client -> client.getClientId())
                .collect(Collectors.toUnmodifiableSet());
        if (skipUserConsentClientIds.contains(currentClientId)) {
            throw new InvalidDataException("Client.skipUserConsent.clients.invalid");
        }
        for (String skipUserConsentClientId : skipUserConsentClientIds) {
            if (!existingClientIds.contains(skipUserConsentClientId)) {
                throw new InvalidDataException("Client.skipUserConsent.clients.missing");
            }
        }
    }

    private void validateName(Client client) {
        if (adminConfProvider.isSsoMode()) {
            if (StringUtils.isBlank(client.getClientName().getEt())) {
                throw new InvalidDataException("Client.sso.clientName");
            }
            if (StringUtils.isBlank(client.getClientShortName().getEt())) {
                throw new InvalidDataException("Client.sso.clientShortName");
            }
        }
        validateShortName(client.getClientShortName().getEt());
        validateShortName(client.getClientShortName().getEn());
        validateShortName(client.getClientShortName().getRu());
    }

    private void validateShortName(String shortName) {
        if (shortName == null) {
            return;
        }
        if (containsNonUcs2Characters(shortName)) {
            throw new InvalidDataException("Client.shortName.forbiddenCharacters");
        }
        if (shortName.length() > MAX_SHORT_NAME_LENGTH) {
            throw new InvalidDataException("Client.shortName.tooLong");
        }
        if (containsNonGsm7Characters(shortName) && shortName.length() > MAX_SHORT_NAME_GSM7_LENGTH) {
            throw new InvalidDataException("Client.shortName.tooLong");
        }
    }

    private boolean containsNonGsm7Characters(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (GSM_7_CHARACTERS.indexOf(str.charAt(i)) == -1) {
                return true;
            }
        }
        return false;
    }

    private boolean containsNonUcs2Characters(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (! Character.isBmpCodePoint(str.charAt(i)) || Character.isSurrogate(str.charAt(i))) {
                return true;
            }
        }
        return false;
    }

    private void validateScopes(Client client) {
        if (CollectionUtils.isEmpty(client.getScope())) {
            throw new InvalidDataException("Client.scope.missing");
        }
    }

    private void validateRedirectUris(Client client) {
        validateBackchannelLogoutUri(client);
        validatePostLogoutRedirectUris(client);
        client.getRedirectUris().forEach(uri -> validateUri(uri, "Client.redirectUri.missing"));
    }

    private void validateBackchannelLogoutUri(Client client) {
        if (adminConfProvider.isSsoMode()) {
            validateUri(client.getBackchannelLogoutUri(), "Client.backchannelUri.missing");
        } else {
            if (StringUtils.isNotBlank(client.getBackchannelLogoutUri())) {
                throw new IllegalStateException("Backchannel logout uri must not be set in TARA mode");
            }
        }
    }

    private void validatePostLogoutRedirectUris(Client client) {
        if (adminConfProvider.isSsoMode()) {
            List<String> postLogoutRedirectUris = client.getPostLogoutRedirectUris();
            if (postLogoutRedirectUris == null || postLogoutRedirectUris.isEmpty()) {
                throw new InvalidDataException("Client.postLogoutRedirectUri.missing");
            }
            postLogoutRedirectUris.forEach(uri -> validateUri(uri, "Client.postLogoutRedirectUri.missing"));

        } else {
            if (!CollectionUtils.isEmpty(client.getPostLogoutRedirectUris())) {
                throw new IllegalStateException("Post logout redirect uris must not be set in TARA mode");
            }
        }
    }

    private void validateUri(String uri, String errMsg) {
        try {
            URL url = new URL(uri);
            String protocol = url.getProtocol();
            String userInfo = url.getUserInfo();
            String fragment = url.toURI().getRawFragment();
            if (userInfo != null || !protocol.equals("https") || fragment != null) {
                throw new InvalidDataException(errMsg);
            }
        } catch (URISyntaxException | MalformedURLException ex) {
            throw new InvalidDataException(errMsg);
        }
    }

    private void validateEidasRequesterId(Client client) {
        if (adminConfProvider.isSsoMode()) {
            if (StringUtils.isNotBlank(client.getEidasRequesterId())) {
                throw new IllegalStateException("eIDAS RequesterID must not be set in SSO mode");
            }
        } else {
            if (StringUtils.isBlank(client.getEidasRequesterId())) {
                throw new InvalidDataException("Client.eidasRequesterId.missing");
            }
            validateClientWithEidasRequesterIdDoesNotExist(client);
        }
    }

    /*
        Given database field has a unique constraint thus this check should not be necessary,
        but catching that exception during insertion is a problem with @Transactional function.
        TODO: catch the exception during saving and throw "Client.eidasRequesterId.exists" from there.
     */
    private void validateClientWithEidasRequesterIdDoesNotExist(Client client) {
        ee.ria.tara.repository.model.Client existingClientWithEidasRequesterId = clientRepository
                .findByEidasRequesterId(client.getEidasRequesterId());
        if (existingClientWithEidasRequesterId != null &&
                !StringUtils.equals(existingClientWithEidasRequesterId.getClientId(), client.getClientId())) {
            throw new InvalidDataException("Client.eidasRequesterId.exists");
        }
    }
    private void validateLogo(byte[] logo) {
        if (adminConfProvider.isSsoMode()) {
            if (logo != null && logo.length > LOGO_ALLOWED_MAX_SIZE_IN_BYTES) {
                throw new InvalidDataException("Client.logo.tooLarge");
            }
        } else {
            if (logo != null) {
                throw new IllegalStateException("Client logo must not be set in TARA mode");
            }
        }
    }

    private void validatePaasukeQueryParameters(Client client) {
        List<String> scope = client.getScope();
        boolean containsRepresenteeScope = scope.contains(SCOPE_REPRESENTEE) || scope.contains(SCOPE_REPRESENTEE_LIST);
        if (adminConfProvider.isSsoMode()) {
            if (containsRepresenteeScope && (client.getPaasukeParameters() == null || !client.getPaasukeParameters().matches(VALID_PAASUKE_PARAMS_PATTERN))) {
                throw new InvalidDataException("Client.paasukeParameters.invalid");
            } else if (!containsRepresenteeScope && client.getPaasukeParameters() != null) {
                throw new IllegalStateException("Paasuke parameters must not be set without representee.* or representee_list scope");
            }
        } else if (StringUtils.isNotBlank(client.getPaasukeParameters())) {
            throw new IllegalStateException("Paasuke parameters must not be set in TARA mode");
        }

    }
    private void validateAccessTokenJwtEnabled(Client client) {
        if (!adminConfProvider.isSsoMode() && client.getAccessTokenJwtEnabled()) {
            throw new IllegalStateException("Access token JWT enabled must not be set to true in TARA mode");
        }
    }

    private void validateAccessTokenAudienceUris(Client client) {
        if (adminConfProvider.isSsoMode()) {
            if (client.getAccessTokenJwtEnabled()) {
                if (CollectionUtils.isEmpty(client.getAccessTokenAudienceUris())) {
                    throw new InvalidDataException("Client.accessTokenAudienceUri.missing");
                }
                client.getAccessTokenAudienceUris().forEach(uri -> validateUri(uri, "Client.accessTokenAudienceUri.missing"));
            } else if (!CollectionUtils.isEmpty(client.getAccessTokenAudienceUris())) {
                throw new IllegalStateException("Access token audience uris must not be set if access token JWT strategy is not enabled");
            }
        } else {
            if (!CollectionUtils.isEmpty(client.getAccessTokenAudienceUris())) {
                throw new IllegalStateException("JWT service uris must not be set in TARA mode");
            }
        }
    }

    private void validateAccessTokenLifespan(Client client) {
        if (client.getAccessTokenLifespan() == null) {
            return;
        }
        if (!adminConfProvider.isSsoMode()) {
            throw new IllegalStateException("JWT access token lifespan must not be set in TARA mode");
        }
        if (!client.getAccessTokenJwtEnabled()) {
            throw new IllegalStateException("JWT access token lifespan must not be set if access token JWT strategy is not enabled");
        }
        validateAccessTokenLifespanLimits(client.getAccessTokenLifespan());
    }

    private void validateAccessTokenLifespanLimits(String accessTokenLifespan) {
        Duration duration = HydraDurationHelper.toDuration(accessTokenLifespan);

        if (duration.compareTo(MIN_ALLOWED_ACCESS_TOKEN_LIFESPAN) < 0) {
            throw new InvalidDataException(
                "Client.accessTokenLifespan.subceedsMinAllowedDuration",
                HydraDurationHelper.format(MIN_ALLOWED_ACCESS_TOKEN_LIFESPAN)
            );
        }

        Duration maxDuration = adminConfProvider.getMaxAccessTokenLifespan();
        if (duration.compareTo(maxDuration) > 0) {
            throw new InvalidDataException(
                "Client.accessTokenLifespan.exceedsMaxAllowedDuration",
                HydraDurationHelper.format(maxDuration)
            );
        }
    }
}
