package com.example.kioskprototype.adapterView;


import java.io.Serializable;

public class PoiObject1 implements Serializable {
    private int id;
    private int type;
    private String name;
    private String address;
    private float distance;
    private  String description;

    public int getId() {
        return id;
    }

    public int getType() {
        return type;
    }

    public PoiObject1(int id, String name, String address, float distance, String description, int type){
        this.id = id;
        this.type = type;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public float getDistance() {
        return distance;
    }

    public void setDistance(float distance) {
        this.distance = distance;
    }

    public String getDescription(){
        return description;
    }
}
