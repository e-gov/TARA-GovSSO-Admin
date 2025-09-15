package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;
import static ee.ria.tara.model.InstitutionType.TypeEnum.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class GovSsoScopeFilterTest {

    private final GovSsoScopeFilter scopeFilter = new GovSsoScopeFilter();

    @Nested
    class PrivateInstitution {

        @Test
        public void filterInstitutionClientScopes_emptyListReturned() {
            List<String> clientScopes = List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PRIVATE);

            assertEquals(List.of(), filteredScopes);
        }

    }

    @Nested
    class PublicInstitution {

        @Test
        public void filterInstitutionClientScopes_whenScopesContainDisallowedScopes_onlyAllowedScopesReturned() {
            List<String> clientScopes = List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value", "representee.*",
                    "representee_list");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of("openid", "email", "phone", "representee.*", "representee_list"), filteredScopes);
        }

        @Test
        public void filterInstitutionClientScopes_whenScopesEmpty_emptyListReturned() {
            List<String> clientScopes = List.of();

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of(), filteredScopes);
        }

        @Test
        public void filterInstitutionClientScopes_whenRepeatedScopes_inputWithoutRecurrencesReturned() {
            List<String> clientScopes = List.of(
                    "openid", "email", "email", "openid");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of("openid", "email"), filteredScopes);
        }

    }

}
