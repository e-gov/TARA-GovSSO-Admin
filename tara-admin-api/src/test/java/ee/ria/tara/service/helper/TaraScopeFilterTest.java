package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;
import static ee.ria.tara.model.InstitutionType.TypeEnum.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ActiveProfiles("test")
public class TaraScopeFilterTest {

    private final TaraScopeFilter scopeFilter = new TaraScopeFilter();

    @Nested
    class PrivateInstitution {

        @Test
        public void filterInstitutionClientScopes_whenPrivateInstitution_whenScopesContainDisallowedScopes_onlyAllowedScopesReturned() {
            List<String> clientScopes = List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PRIVATE);

            assertEquals(List.of("openid", "eidas", "eidasonly", "eidas:country:*"), filteredScopes);
        }

    }

    @Nested
    class PublicInstitution {

        @Test
        public void filterInstitutionClientScopes_whenScopesContainDisallowedScopes_onlyAllowedScopesReturned() {
            List<String> clientScopes = List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson"), filteredScopes);
        }

        @Test
        public void filterInstitutionClientScopes_whenScopesInRandomOrder_scopesReturnedInFixedOrder() {
            List<String> clientScopes = List.of(
                    "idcard", "eidas", "legalperson", "openid", "eidasonly", "eidas:country:*", "mid",
                    "smartid", "phone", "email");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of(
                    "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                    "smartid", "email", "phone", "legalperson"), filteredScopes);
        }

    }

}
