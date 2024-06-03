package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.model.InstitutionType;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ScopeFilter {

    private final AdminConfigurationProvider adminConfProvider;

    private static final List<String> PRIVATE_TARA_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            Arrays.asList("openid", "eidas", "eidasonly", "eidas:country:*");
    private static final List<String> PUBLIC_TARA_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            Arrays.asList("openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid", "smartid", "email", "phone", "legalperson");

    private static final List<String> PRIVATE_SSO_INSTITUTION_CLIENT_ALLOWED_SCOPES = List.of();
    private static final List<String> PUBLIC_SSO_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            Arrays.asList("openid", "email", "phone", "representee.*", "representee_list");

    public List<String> filterInstitutionClientScopes(List<String> clientScopes, InstitutionType.TypeEnum institutionType) {
        List<String> allowedScopes = adminConfProvider.isSsoMode() ?
                (institutionType == InstitutionType.TypeEnum.PRIVATE ?
                        PRIVATE_SSO_INSTITUTION_CLIENT_ALLOWED_SCOPES :
                        PUBLIC_SSO_INSTITUTION_CLIENT_ALLOWED_SCOPES) :
                (institutionType == InstitutionType.TypeEnum.PRIVATE ?
                        PRIVATE_TARA_INSTITUTION_CLIENT_ALLOWED_SCOPES :
                        PUBLIC_TARA_INSTITUTION_CLIENT_ALLOWED_SCOPES);

        return allowedScopes.stream()
                .filter(clientScopes::contains)
                .collect(Collectors.toList());
    }
}
