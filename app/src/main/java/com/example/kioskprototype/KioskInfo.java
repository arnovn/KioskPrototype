package com.example.kioskprototype;

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
     * Constructor of the KioskInfo class
     */
    private KioskInfo(){
        id = 1;
        name = "Kiosk station leuven";
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
}
