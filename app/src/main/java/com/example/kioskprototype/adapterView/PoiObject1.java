package com.example.kioskprototype.adapterView;


import java.io.Serializable;

/**
 * Converts rows from Poi-table of MySql Database to objects.
 */
public class PoiObject1 implements Serializable {

    /**
     * Poi id
     */
    private int id;

    /**
     * Poi type (e.g. 1 = Restaurant)
     */
    private int type;

    /**
     * Poi name
     */
    private String name;

    /**
     * Poi address
     */
    private String address;

    /**
     * Poi distance from Kiosk
     */
    private float distance;

    /**
     * Description of Poi
     */
    private  String description;

    /**
     * Latitude of Poi
     */
    private double latitude;

    /**
     * Longitude of Poi
     */
    private double longitude;

    /**
     * Constructor of the PoiObject
     * @param id
     *              id of the Poi from the MySql Database
     * @param name
     *              name of the Poi from the MySql Database
     * @param address
     *              address of the Poi from the MySql Database
     * @param distance
     *              distance of the Poi from the MySql Database
     * @param description
     *              description of the Poi from the MySql Database
     * @param type
     *              Type of the Poi from the MySql Database
     */
    public PoiObject1(int id, String name, String address, float distance, String description, int type, double latitude, double longitude){
        this.id = id;
        this.type = type;
        this.name = name;
        this.address = address;
        this.distance = distance;
        this.description = description;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    /**
     * Getter for the Poi name
     * @return
     *          Name of the poi
     */
    public String getName() {
        return name;
    }

    /**
     * Id getter
     * @return
     *          Id of PoiObject
     */
    public int getId() {
        return id;
    }

    /**
     * Type getter
     * @return
     *          Type of the type id Poi
     */
    public int getType() {
        return type;
    }

    /**
     * Type getter
     * @return
     *          Type of the string name Poi type
     */
    public String getStringType() {
        switch (type){
            case 1:
                return "Restaurant";
            case 2:
                return "Worth to visit";
            case 3:
                return "Route";
            case 4:
                return "Activity";
        }
        return "Error: type doesn't exist yet";
    }

    /**
     * Setter of Poi name
     * @param name
     *              New poi name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter of Poi address
     * @return
     *              Poi address
     */
    public String getAddress() {
        return address;
    }

    /**
     * Setter of Poi address
     * @param address
     *              New Poi address
     */
    public void setAddress(String address) {
        this.address = address;
    }

    /**
     * Poi distance from Kiosk getter
     * @return
     *              Distance of Poi from Kiosk
     */
    public float getDistance() {
        return distance;
    }

    /**
     * Poi distance setter from Kiosk
     * @param distance
     *              Distance of Poi from Kiosk
     */
    public void setDistance(float distance) {
        this.distance = distance;
    }

    /**
     * Getter of description of Poi
     * @return
     *              Description of Poi
     */
    public String getDescription(){
        return description;
    }

    /**
     * Getter of latitude Poi
     * @return
     *              Latitude of Poi
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Getter of longitude Poi
     * @return
     *              Longitude of Poi
     */
    public double getLongitude() {
        return longitude;
    }
}
