package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS_COUNTRY;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS_ONLY;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EMAIL;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_IDCARD;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_LEGALPERSON;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_MID;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_OPENID;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_PHONE;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_SMARTID;

@Service
@ConditionalOnProperty(value = "tara.admin.sso-mode", havingValue = "false")
@RequiredArgsConstructor
public class TaraScopeFilter implements ScopeFilter {

    private static final List<String> PRIVATE_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            List.of(SCOPE_OPENID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY);
    private static final List<String> PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            List.of(SCOPE_OPENID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY, SCOPE_IDCARD, SCOPE_MID, SCOPE_SMARTID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_LEGALPERSON);

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
