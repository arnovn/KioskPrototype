package com.example.kioskprototype;

//TODO: connection with database so data is pulled from there on startup.

/**
 * Singleton class containing the name and id of the kiosk (same as in the MySql Database)
 * Should be unique per Kiosk
 */
public class KioskInfo {

    /**
     * Name of the Kiosk
     */
    private String name;

    /**
     * Id of the Kiosk
     */
    private int id;

    /**
     * Longitude of the Kiosk
     */
    double longitude;

    /**
     * Latitude of the Kiosk
     */
    double latitude;

    /**
     * Constructor of the KioskInfo class
     */
    private KioskInfo(){
        id = 1;
        name = "Kiosk station leuven";
        longitude = 4.71556200;
        latitude = 50.88229700;
       /* id = 2;
        name = "Kiosk station leuven";
        longitude = 4.66504400;
        latitude = 50.86130500;*/

    }

    /**
     * Singleton Kiosk object
     */
    private static KioskInfo mKioskInfo;

    /**
     * Singleton Kiosk initializer
     * @return
     *          Kiosk object
     */
    public static KioskInfo get(){
        if(mKioskInfo == null){
            mKioskInfo = new KioskInfo();
        }
        return mKioskInfo;
    }

    /**
     * Id getter
     * @return
     *          Id of the kiosk
     */
    public int getId(){
        return id;
    }

    /**
     * Name getter
     * @return
     *          Name of the Kiosk
     */
    public String getName(){
        return name;
    }

    /**
     * Getter for longitude
     * @return
     *          Kiosk longitude
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * Getter for latitude
     * @return
     *          Kiosk latitude
     */
    public double getLatitude() {
        return latitude;
    }
}
