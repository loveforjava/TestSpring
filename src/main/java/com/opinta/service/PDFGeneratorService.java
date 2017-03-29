package com.opinta.service;

import com.opinta.entity.User;
import javax.naming.AuthenticationException;

public interface PDFGeneratorService {

    byte[] generateLabel(long id, User user) throws AuthenticationException;

    byte[] generatePostpay(long id, User user) throws AuthenticationException;
}
