package ee.ria.tara.service.helper;

import ee.ria.tara.model.InstitutionBillingSettings;
import ee.ria.tara.model.InstitutionMetainfo;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.model.Client;
import ee.ria.tara.repository.model.Institution;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

public class InstitutionHelper {
    public static Institution convertToEntity(ee.ria.tara.model.Institution institution) {
        String id = institution.getId();
        Institution entity = new Institution();
        entity.setId(id == null ? null : Long.valueOf(id));
        entity.setRegistryCode(institution.getRegistryCode());
        entity.setName(institution.getName());
        entity.setClients(List.of());
        entity.setContactAddress(institution.getAddress());
        entity.setContactPhone(institution.getPhone());
        entity.setContactEmail(institution.getEmail());
        entity.setType(institution.getType().getType());
        entity.setBillingEmail(institution.getBillingSettings().getEmail());

        if (institution.getCreatedAt() != null) {
            entity.setCreatedAt(institution.getCreatedAt());
        }

        if (institution.getUpdatedAt() != null) {
            entity.setUpdatedAt(institution.getUpdatedAt());
        }

        return entity;
    }

    public static ee.ria.tara.model.Institution convertFromEntity(Institution entity) {
        ee.ria.tara.model.Institution institution = new ee.ria.tara.model.Institution();
        InstitutionType institutionType = new InstitutionType();
        InstitutionBillingSettings billingSettings = new InstitutionBillingSettings();

        institutionType.setType(entity.getType());
        billingSettings.setEmail(entity.getBillingEmail());

        institution.setId(entity.getId().toString());
        institution.setRegistryCode(entity.getRegistryCode());
        institution.setName(entity.getName());
        institution.setClientIds(entity.getClients().stream().map(Client::getClientId).collect(Collectors.toList()));
        institution.setAddress(entity.getContactAddress());
        institution.setPhone(entity.getContactPhone());
        institution.setEmail(entity.getContactEmail());
        institution.setType(institutionType);

        institution.setBillingSettings(billingSettings);
        institution.setCreatedAt(entity.getCreatedAt());
        institution.setUpdatedAt(entity.getUpdatedAt());

        return institution;
    }

    public static InstitutionMetainfo convertToInstitutionMetainfo(Institution entity) {
        InstitutionMetainfo metainfo = new InstitutionMetainfo();
        InstitutionType institutionType = new InstitutionType();

        institutionType.setType(entity.getType());

        metainfo.setRegistryCode(entity.getRegistryCode());
        metainfo.setName(entity.getName());
        metainfo.setType(institutionType);

        return metainfo;
    }
}
