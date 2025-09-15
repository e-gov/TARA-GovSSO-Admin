package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionType;

import java.util.List;

public interface ScopeFilter {

    List<String> filterInstitutionClientScopes(List<String> clientScopes, InstitutionType.TypeEnum institutionType);

}
