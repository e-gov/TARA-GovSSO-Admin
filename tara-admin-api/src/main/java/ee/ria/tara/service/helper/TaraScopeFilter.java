package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@ConditionalOnProperty(value = "tara.admin.sso-mode", havingValue = "false")
@RequiredArgsConstructor
public class TaraScopeFilter implements ScopeFilter {

    private static final List<String> PRIVATE_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            List.of("openid", "eidas", "eidasonly", "eidas:country:*");
    private static final List<String> PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            List.of("openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid", "smartid", "email", "phone", "legalperson");

    public List<String> filterInstitutionClientScopes(List<String> clientScopes, InstitutionType.TypeEnum institutionType) {
        List<String> allowedScopes =
                institutionType == InstitutionType.TypeEnum.PRIVATE ?
                        PRIVATE_INSTITUTION_CLIENT_ALLOWED_SCOPES :
                        PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES;

        return allowedScopes.stream()
                .filter(clientScopes::contains)
                .collect(Collectors.toList());
    }
}
