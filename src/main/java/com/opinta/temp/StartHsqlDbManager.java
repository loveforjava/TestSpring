package com.opinta.temp;

import javax.annotation.PostConstruct;

import org.springframework.stereotype.Service;

@Service
public class StartHsqlDbManager {
    @PostConstruct
    public void startDBManager() {
        //DatabaseManagerSwing.main(new String[]{"--url", "jdbc:hsqldb:mem:testdb", "--user", "sa", "--password", "sa"});
    }
}
