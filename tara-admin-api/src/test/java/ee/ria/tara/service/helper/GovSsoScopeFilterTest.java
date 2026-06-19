package ee.ria.tara.service.helper;

import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS_COUNTRY;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EIDAS_ONLY;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_EMAIL;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_IDCARD;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_LEGALPERSON;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_MID;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_OPENID;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_PHONE;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_REPRESENTEE;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_REPRESENTEE_LIST;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_AUTH_HANDOVER;
import static ee.ria.tara.service.helper.ClientScopes.SCOPE_SMARTID;
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
                    SCOPE_OPENID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY, SCOPE_IDCARD, SCOPE_MID,
                    SCOPE_SMARTID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_LEGALPERSON, "invalid_value", "unknown_value");

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PRIVATE);

            assertEquals(List.of(), filteredScopes);
        }

    }

    @Nested
    class PublicInstitution {

        @Test
        public void filterInstitutionClientScopes_whenScopesContainDisallowedScopes_onlyAllowedScopesReturned() {
            List<String> clientScopes = List.of(
                    SCOPE_OPENID, SCOPE_EIDAS, SCOPE_EIDAS_ONLY, SCOPE_EIDAS_COUNTRY, SCOPE_IDCARD, SCOPE_MID,
                    SCOPE_SMARTID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_LEGALPERSON, "invalid_value", "unknown_value", SCOPE_REPRESENTEE,
                    SCOPE_REPRESENTEE_LIST, SCOPE_AUTH_HANDOVER);

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of(SCOPE_OPENID, SCOPE_EMAIL, SCOPE_PHONE, SCOPE_REPRESENTEE, SCOPE_REPRESENTEE_LIST, SCOPE_AUTH_HANDOVER), filteredScopes);
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
                    SCOPE_OPENID, SCOPE_EMAIL, SCOPE_EMAIL, SCOPE_OPENID);

            List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

            assertEquals(List.of(SCOPE_OPENID, SCOPE_EMAIL), filteredScopes);
        }

    }

}
