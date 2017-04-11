package com.opinta.service;

import com.opinta.exception.AuthException;
import com.opinta.exception.IncorrectInputDataException;
import java.io.IOException;
import java.util.UUID;
import com.opinta.entity.User;

public interface PDFGeneratorService {

    byte[] generateShipmentGroupForms(UUID shipmentGroupUuid, User user) throws AuthException,
            IncorrectInputDataException, IOException;

    byte[] generateForm103(UUID shipmentGroupUuid, User user) throws AuthException,
            IncorrectInputDataException, IOException;

    byte[] generateShipmentForm(UUID id, User user) throws AuthException, IncorrectInputDataException, IOException;
}
