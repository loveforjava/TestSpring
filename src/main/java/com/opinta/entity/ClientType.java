package com.opinta.entity;

public enum ClientType {
    INDIVIDUAL ("P"),
    COMPANY ("L"),
    EMPLOYEE ("Z");
        
    private final String postIdLetter;
    
    ClientType(String postIdLetter) {
        this.postIdLetter = postIdLetter;
    }
    
    public String postIdLetter() {
        return this.postIdLetter;
    }
}
