package com.opinta.model;

public enum DeliveryType {
    W2W("Warehouse to Warehouse"),
    W2D("Warehouse to Door"),
    D2W("Door to Warehause"),
    D2D("Door to Door");

    private String name;

    private DeliveryType(String name){
        this.name = name;
    }

    public String getName(){
        return name;
    }
}
