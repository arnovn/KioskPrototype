package com.example.kioskprototype.adapterView;

import java.io.Serializable;

public class ABikeObject implements Serializable {
    private int id;
    private int type;
    private double batteryLevel;
    private double latitude;
    private double longitude;
    private int code;
    private int bikeStand;

    public ABikeObject(int id, int type,double batteryLevel, double latitude, double longitude, int code, int bikeStand){
        this.id = id;
        this.type = type;
        this.batteryLevel = batteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.bikeStand = bikeStand;

    }

    public int getId() {
        return id;
    }

    public double getBatteryLevel() {
        return batteryLevel;
    }

    public int getBikeStand() {
        return bikeStand;
    }

    public int getType() {
        return type;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getCode() {
        return code;
    }
}
