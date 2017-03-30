package com.opinta.service;

import com.opinta.entity.User;
import javax.naming.AuthenticationException;

public interface PDFGeneratorService {

    byte[] generate(long id, User user) throws Exception;

}