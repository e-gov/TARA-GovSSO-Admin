package ee.ria.tara.service.helper;

import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionBillingSettings;
import ee.ria.tara.model.InstitutionType;

import java.time.OffsetDateTime;
import java.util.List;

public class InstitutionTestHelper {
    public static Institution createTestInstitution() {
        Institution institution = new Institution();
        InstitutionType institutionType = new InstitutionType();
        InstitutionBillingSettings billingSettings = new InstitutionBillingSettings();

        billingSettings.setEmail("billing@example.com");
        institutionType.setType(InstitutionType.TypeEnum.PUBLIC);

        institution.setId("1");
        institution.setRegistryCode("12345678");
        institution.setName("Institution");
        institution.setEmail("info@example.com");
        institution.setAddress("address");
        institution.setPhone("123456");
        institution.setType(institutionType);
        institution.setClientIds(List.of());
        institution.setBillingSettings(billingSettings);
        institution.setCreatedAt(OffsetDateTime.now());
        institution.setUpdatedAt(OffsetDateTime.now());

        return institution;
    }
}
