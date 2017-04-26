package ua.ukrpost.service;

import ua.ukrpost.exception.AuthException;
import ua.ukrpost.exception.IncorrectInputDataException;
import java.io.IOException;
import java.util.UUID;
import ua.ukrpost.entity.User;

public interface PDFGeneratorService {

    byte[] generateShipmentGroupForms(UUID shipmentGroupUuid, User user) throws AuthException,
            IncorrectInputDataException, IOException;

    byte[] generateForm103(UUID shipmentGroupUuid, User user) throws AuthException,
            IncorrectInputDataException, IOException;

    byte[] generateShipmentForm(UUID id, User user) throws AuthException, IncorrectInputDataException, IOException;
}
