package com.opinta.service;

import java.util.UUID;
import com.opinta.entity.User;
import javax.naming.AuthenticationException;

public interface PDFGeneratorService {

    byte[] generate(UUID id, User user) throws Exception;
}