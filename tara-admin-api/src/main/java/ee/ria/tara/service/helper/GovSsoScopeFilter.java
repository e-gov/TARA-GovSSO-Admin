package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EMAIL;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_OPENID;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_PHONE;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_REPRESENTEE;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_REPRESENTEE_LIST;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_AUTH_HANDOVER;
import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;

@Service
@ConditionalOnProperty(value = "tara.admin.sso-mode", havingValue = "true")
@RequiredArgsConstructor
public class GovSsoScopeFilter implements ScopeFilter {

    private static final List<String> PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            Arrays.asList(SCOPE_OPENID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST, SCOPE_AUTH_HANDOVER);

    public List<String> filterInstitutionClientScopes(List<String> clientScopes, InstitutionType.TypeEnum institutionType) {
        if (institutionType == PRIVATE) {
            return List.of();
        }
        return PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES.stream()
                .filter(clientScopes::contains)
                .collect(Collectors.toList());
    }
}
