package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;

import static ee.ria.tara.model.InstitutionType.TypeEnum.PRIVATE;
import static ee.ria.tara.model.InstitutionType.TypeEnum.PUBLIC;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.doReturn;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class ScopeFilterTest {

    @Mock
    private AdminConfigurationProvider adminConfigurationProvider;

    @InjectMocks
    private ScopeFilter scopeFilter;

    @Test
    public void filterInstitutionClientScopes_ssoPrivateInstitutionWithNotAllowedScopes_notAllowedScopesRemoved() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        List<String> clientScopes = List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

        List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PRIVATE);

        assertEquals(List.of(), filteredScopes);
    }

    @Test
    public void filterInstitutionClientScopes_ssoPublicInstitutionWithNotAllowedScopes_notAllowedScopesRemoved() {
        doReturn(true).when(adminConfigurationProvider).isSsoMode();

        List<String> clientScopes = List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value", "representee");

        List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

        assertEquals(List.of("openid", "email", "phone", "representee"), filteredScopes);
    }

    @Test
    public void filterInstitutionClientScopes_taraPublicInstitutionWithNotAllowedScopes_notAllowedScopesRemoved() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        List<String> clientScopes = List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

        List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

        assertEquals(List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson"), filteredScopes);
    }

    @Test
    public void filterInstitutionClientScopes_taraPrivateInstitutionWithNotAllowedScopes_notAllowedScopesRemoved() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        List<String> clientScopes = List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson", "invalid_value", "unknown_value");

        List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PRIVATE);

        assertEquals(List.of("openid", "eidas", "eidasonly", "eidas:country:*"), filteredScopes);
    }

    @Test
    public void filterInstitutionClientScopes_taraPrivateInstitutionWithUnorderedScopes_scopesOrdered() {
        doReturn(false).when(adminConfigurationProvider).isSsoMode();

        List<String> clientScopes = List.of(
                "idcard", "eidas", "legalperson", "openid", "eidasonly", "eidas:country:*", "mid",
                "smartid", "phone", "email");

        List<String> filteredScopes = scopeFilter.filterInstitutionClientScopes(clientScopes, PUBLIC);

        assertEquals(List.of(
                "openid", "eidas", "eidasonly", "eidas:country:*", "idcard", "mid",
                "smartid", "email", "phone", "legalperson"), filteredScopes);
    }
}
