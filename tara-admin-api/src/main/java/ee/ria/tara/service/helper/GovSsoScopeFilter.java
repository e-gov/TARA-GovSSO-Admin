package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionType;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;

@Service
@ConditionalOnProperty(value = "tara.admin.sso-mode", havingValue = "true")
@RequiredArgsConstructor
public class GovSsoScopeFilter implements ScopeFilter {

    private static final List<String> PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES =
            Arrays.asList("openid", "email", "phone", "representee.*", "representee_list");

    public List<String> filterInstitutionClientScopes(List<String> clientScopes, InstitutionType.TypeEnum institutionType) {
        if (institutionType == PRIVATE) {
            return List.of();
        }
        return PUBLIC_INSTITUTION_CLIENT_ALLOWED_SCOPES.stream()
                .filter(clientScopes::contains)
                .collect(Collectors.toList());
    }
}
