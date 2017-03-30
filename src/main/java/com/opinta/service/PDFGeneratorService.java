package com.opinta.service;

import java.util.UUID;
import com.opinta.entity.User;
import javax.naming.AuthenticationException;

public interface PDFGeneratorService {

    byte[] generateLabel(UUID id, User user) throws AuthenticationException;

    byte[] generatePostpay(UUID id, User user) throws AuthenticationException;
}
