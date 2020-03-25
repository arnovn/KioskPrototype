package com.example.kioskprototype.adapterView;

import java.io.Serializable;

/**
 * When the bike table is retrieved from the MySql database, bike objects can be made based on the data.
 */
public class ABikeObject implements Serializable {
    /**
     * Id of the BikeObject
     */
    private int id;

    /**
     * Type of the BikeObject
     */
    private int type;

    /**
     * Batterylevel of the BikeObject
     */
    private double batteryLevel;

    /**
     * Latitude of the BikeObject
     */
    private double latitude;

    /**
     * Longitude of the BikeObject
     */
    private double longitude;

    /**
     * Unlock code of the BikeObject
     */
    private int code;

    /**
     * BikeStand of the BikeObject
     */
    private int bikeStand;

    /**
     * Constructor of the bike object
     * @param id
     *          Id of the bike in the bikes table of the MySql database.
     * @param type
     *          Type of the bike in the bikes table of the MySql database.
     * @param batteryLevel
     *          Batterylevel of the bike in the bikes table of the MySql database.
     * @param latitude
     *          Latitude of the bike in the bikes table of the MySql database.
     * @param longitude
     *          Unlock code of the bike in the bikes table of the MySql database.
     * @param code
     *          Unlock code of the bike in the bikes table of the MySql database.
     * @param bikeStand
     *          BikeStand of the bike in the bikes table of the MySql database.
     */
    public ABikeObject(int id, int type,double batteryLevel, double latitude, double longitude, int code, int bikeStand){
        this.id = id;
        this.type = type;
        this.batteryLevel = batteryLevel;
        this.latitude = latitude;
        this.longitude = longitude;
        this.code = code;
        this.bikeStand = bikeStand;

    }

    /**
     * Id getter
     * @return
     *      id of the BikeObject
     */
    public int getId() {
        return id;
    }

    /**
     * Battery level getter
     * @return
     *      Battery level of the BikeObject
     */
    public double getBatteryLevel() {
        return batteryLevel;
    }

    /**
     * Bike stand getter
     * @return
     *      Bike stand of the BikeObject
     */
    public int getBikeStand() {
        return bikeStand;
    }

    /**
     * Type getter
     * @return
     *      Type of the BikeObject
     */
    public int getType() {
        return type;
    }

    /**
     * Latitude getter
     * @return
     *      Latitude of the BikeObject
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Lonitude getter
     * @return
     *      Longitude of the BikeObject
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Code getter
     * @return
     *      Unlock code of the BikeObject
     */
    public int getCode() {
        return code;
    }
}
