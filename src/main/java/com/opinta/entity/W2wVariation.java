package com.opinta.entity;

public enum W2wVariation {
    TOWN("Inside of the region or Kyiv"),
    REGION("Between region centers"),
    COUNTRY("All county");

    private String name;

    W2wVariation(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
