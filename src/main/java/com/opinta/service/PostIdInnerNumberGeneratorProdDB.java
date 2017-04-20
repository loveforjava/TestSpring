package com.opinta.service;

import com.opinta.dao.ClientDao;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@Profile({"stage", "dev"})
public class PostIdInnerNumberGeneratorProdDB implements PostIdInnerNumberGenerator {
    private final ClientDao clientDao;
    
    @Autowired
    public PostIdInnerNumberGeneratorProdDB(ClientDao clientDao) {
        this.clientDao = clientDao;
    }
    
    @Override
    public String generateNextNumber() {
        return clientDao.getNextPostIdInnerNumber();
    }
}
