package ee.ria.tara.service;

import ee.ria.tara.controllers.exception.ApiException;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Institution;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.repository.InstitutionRepository;
import ee.ria.tara.service.helper.InstitutionHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import static ee.ria.tara.service.helper.InstitutionHelper.convertToInstitutionMetainfo;

@Slf4j
@Service
public class InstitutionsService {
    private final InstitutionRepository institutionRepository;

    public InstitutionsService(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }

    public List<InstitutionMetainfo> getInstitutionsMetainfo() {
        List<InstitutionMetainfo> metainfoList = new ArrayList<>();

        institutionRepository.findAll()
                .forEach(entity -> metainfoList.add(convertToInstitutionMetainfo(entity)));

        metainfoList.sort(Comparator.comparing(InstitutionMetainfo::getName, String.CASE_INSENSITIVE_ORDER));

        return metainfoList;
    }

    public List<Institution> getInstitutions(String filterBy) {
        List<Institution> institutions = new ArrayList<>();

        if (filterBy != null) {
            institutionRepository.findAllByRegistryCodeContainingIgnoreCaseOrNameContainingIgnoreCase(filterBy, filterBy)
                    .forEach(entity -> institutions.add(InstitutionHelper.convertFromEntity(entity)));
        } else {
            institutionRepository.findAll()
                    .forEach(entity -> institutions.add(InstitutionHelper.convertFromEntity(entity)));
        }

        return institutions;
    }

    public void addInstitution(Institution institution) throws ApiException {
        log.info(String.format("Adding institution with registry_code %s.", institution.getRegistryCode()));

        this.saveInstitution(institution);
    }

    public void updateInstitution(Institution institution) throws ApiException {
        log.info(String.format("Updating institution with registry_code %s.", institution.getRegistryCode()));

        this.saveInstitution(institution);
    }

    public void deleteInstitution(String registryCode) throws ApiException {
        log.info(String.format("Deleting institution with registry_code %s.", registryCode));
        try {
            institutionRepository.deleteByRegistryCode(registryCode);
        } catch (DataIntegrityViolationException ex) {
            log.error(String.format("Failed to delete institution: %s.", registryCode), ex);
            throw new InvalidDataException("Institution.clients.exists");
        }
    }

    private ee.ria.tara.repository.model.Institution saveInstitution(Institution institution) throws InvalidDataException {
        try {
            return institutionRepository.save(InstitutionHelper.convertToEntity(institution));
        } catch (DataIntegrityViolationException ex) {
            log.error(String.format("Failed to save institution: %s.", institution.getRegistryCode()), ex);
            throw new InvalidDataException("Institution.exists");
        }
    }
}
