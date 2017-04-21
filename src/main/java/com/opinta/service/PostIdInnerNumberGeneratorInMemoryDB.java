package com.opinta.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;

import static java.lang.String.format;

@Slf4j
@Service
@Profile("memory")
public class PostIdInnerNumberGeneratorInMemoryDB implements PostIdInnerNumberGenerator {
    private static int number = 0;
    
    @Override
    public String generate() {
        synchronized (this) {
            number++;
        }
        return format("%07d", number);
    }
}
