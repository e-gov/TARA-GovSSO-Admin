package ee.ria.tara.service.helper;

import ee.ria.tara.configuration.providers.AdminConfigurationProvider;
import ee.ria.tara.controllers.exception.InvalidDataException;
import ee.ria.tara.model.Client;
import ee.ria.tara.model.InstitutionType;
import ee.ria.tara.repository.ClientRepository;
import ee.ria.tara.repository.model.Institution;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ClientValidator {

    private final AdminConfigurationProvider adminConfProvider;
    private final ClientRepository clientRepository;

    public void validateClient(Client client, InstitutionType.TypeEnum institutionType) {
        if (adminConfProvider.isSsoMode() && institutionType == InstitutionType.TypeEnum.PRIVATE) {
            throw new InvalidDataException("Client.sso.privateInstitution");
        }
        validateEidasRequesterId(client);
    }

    private void validateEidasRequesterId(Client client) {
        if (adminConfProvider.isSsoMode()) {
            if (StringUtils.isNotBlank(client.getEidasRequesterId())) {
                throw new IllegalStateException("eIDAS RequesterID must not be set in SSO mode");
            }
        } else {
            if (StringUtils.isBlank(client.getEidasRequesterId())) {
                throw new InvalidDataException("Client.eidasRequesterId.missing");
            }
            validateClientWithEidasRequesterIdDoesNotExist(client);
        }
    }

    /*
        The field has a unique constraint thus this check should not be necessary.
        But catching that exception is a problem with @Transactional function.
        TODO: catch the exception during saving and throw "Client.eidasRequesterId.exists" from there.
     */
    private void validateClientWithEidasRequesterIdDoesNotExist(Client client) {
        ee.ria.tara.repository.model.Client existingClientWithEidasRequesterId = clientRepository
                .findByEidasRequesterId(client.getEidasRequesterId());
        if (existingClientWithEidasRequesterId != null &&
                !StringUtils.equals(existingClientWithEidasRequesterId.getClientId(), client.getClientId())) {
            throw new InvalidDataException("Client.eidasRequesterId.exists");
        }
    }
}
