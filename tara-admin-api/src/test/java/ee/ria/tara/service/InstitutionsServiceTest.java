package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionBillingSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.service.helper.InstitutionHelper;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.test.context.ActiveProfiles;

import java.time.OffsetDateTime;
import java.util.List;

import static ee.ria.tara.service.helper.InstitutionHelper.convertToEntity;
import static ee.ria.tara.service.helper.InstitutionTestHelper.createTestInstitution;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
public class InstitutionsServiceTest {
    @Captor
    private ArgumentCaptor<ee.ria.tara.repository.model.Institution> institutionEntityCaptor;


    private Institution institution = createTestInstitution();

    @Mock
    private InstitutionRepository institutionRepository;

    @InjectMocks
    private InstitutionsService service;

    @Test
    public void testGetInstitutionMetainfo() {
        doReturn(List.of(convertToEntity(institution))).when(institutionRepository).findAll();

        List<InstitutionMetainfo> metainfo = service.getInstitutionsMetainfo();

        Assertions.assertEquals(1, metainfo.size());
        Assertions.assertEquals(institution.getRegistryCode(), metainfo.get(0).getRegistryCode());
    }

    @Test
    public void testGetInstitutions() {
        doReturn(List.of(convertToEntity(institution))).when(institutionRepository).findAll();

        List<Institution> institutions = service.getInstitutions(null);

        Assertions.assertEquals(1, institutions.size());
        verify(institutionRepository, times(1)).findAll();
        verify(institutionRepository, times(0)).findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString());
        this.compareInstitutions(institution, institutions.get(0));
    }

    @Test
    public void testGetInstitutionsWithFilter() {
        String name = "filterBy";
        doReturn(List.of(convertToEntity(institution))).when(institutionRepository).findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(anyString(), anyString());

        List<Institution> institutions = service.getInstitutions(name);

        Assertions.assertEquals(1, institutions.size());
        verify(institutionRepository, times(0)).findAll();
        verify(institutionRepository, times(1)).findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(eq(name), eq(name));
        this.compareInstitutions(institution, institutions.get(0));
    }

    @Test
    public void testAddInstitution() throws ApiException {
        ee.ria.tara.repository.model.Institution entity = InstitutionHelper.convertToEntity(institution);

        doReturn(entity).when(institutionRepository).save(any());

        service.addInstitution(institution);

        verify(institutionRepository, times(1)).save(institutionEntityCaptor.capture());
        this.compareInstitutions(institution, InstitutionHelper.convertFromEntity(institutionEntityCaptor.getValue()));
    }


    @Test
    public void testUpdateInstitution() throws ApiException {
        service.updateInstitution(institution);

        verify(institutionRepository, times(1)).save(institutionEntityCaptor.capture());

        this.compareInstitutions(institution, InstitutionHelper.convertFromEntity(institutionEntityCaptor.getValue()));
    }


    @Test
    public void testDeleteInstitution() throws ApiException {
        String registryCode = "1";

        service.deleteInstitution(registryCode.toString());

        verify(institutionRepository, times(1)).deleteByRegistryCode(registryCode);
    }

    @Test
    public void testAddInstitutionWhenDataIntegrityExceptionThrow() {
        doThrow(new DataIntegrityViolationException("Oops, sorry."))
                .when(institutionRepository).save(any());

        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> service.addInstitution(institution));

        Assertions.assertTrue(exception.getMessage().contains("Institution.exists"));
    }

    @Test
    public void testUpdateInstitutionWhenDataIntegrityExceptionThrow() {
        doThrow(new DataIntegrityViolationException("Oops, sorry."))
                .when(institutionRepository).save(any());

        ApiException exception = Assertions.assertThrows(ApiException.class,
                () -> service.updateInstitution(institution));

        Assertions.assertTrue(exception.getMessage().contains("Institution.exists"));
    }

    @Test
    public void testDeleteInstitutionWhenDataIntegrityExceptionThrow() {
        String registryCode = "1";

        doThrow(new DataIntegrityViolationException("Oops, sorry."))
                .when(institutionRepository).deleteByRegistryCode(registryCode);

        InvalidDataException exception = Assertions.assertThrows(InvalidDataException.class,
                () -> service.deleteInstitution(registryCode));

        Assertions.assertTrue(exception.getMessage().contains("Institution.clients.exists"));
    }

    private void compareInstitutions(Institution initial, Institution retrieved) {
        Assertions.assertEquals(initial.getName(), retrieved.getName());
        Assertions.assertEquals(initial.getRegistryCode(), retrieved.getRegistryCode());
        Assertions.assertEquals(initial.getAddress(), retrieved.getAddress());
        Assertions.assertEquals(initial.getEmail(), retrieved.getEmail());
        Assertions.assertEquals(initial.getPhone(), retrieved.getPhone());
        Assertions.assertEquals(initial.getType().getType(), retrieved.getType().getType());

        Assertions.assertEquals(initial.getClientIds(), retrieved.getClientIds());
        Assertions.assertEquals(initial.getBillingSettings().getEmail(), retrieved.getBillingSettings().getEmail());
        Assertions.assertEquals(initial.getUpdatedAt(), retrieved.getUpdatedAt());
        Assertions.assertEquals(initial.getCreatedAt(), retrieved.getCreatedAt());
    }
}
