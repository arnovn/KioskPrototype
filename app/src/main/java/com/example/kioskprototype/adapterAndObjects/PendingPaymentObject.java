package com.example.kioskprototype.adapterAndObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;

/**
 * Converts rows from Order-table of MySql Database to Payment objects.
 */
public class PendingPaymentObject implements Serializable {
    /**
     * Id of order in MySQL Kiosk_db in bikeorder table
     */
    private int id;

    /**
     * Foreign key: Id of bike in MySQL Kiosk_db in bikeorder table
     * Primary key: Id of bike in MySQL Kiosk_db in bikes table
     */
    private int bikeid;

    /**
     * Start of bike rent, from MySQL Kiosk_db in bikeorder table
     */
    private Date startRent;

    /**
     * End of bike rent, from MySQL Kiosk_db in bikeorder table
     */
    private Date endRent;

    /**
     * Cost of the bike rent, from MySQL Kiosk_db in bikeorder table
     */
    private double amount;

    /**
     * Amount already payed by client, from MySQL Kiosk_db in bikeorder table
     */
    private double amountPayed;


    /**
     * Type of the bike, from MySQL Kiosk_db in bikes table
     */
    private int type;

    /**
     * Price per hour for this type of bike, from MySQL Kiosk_db in bike_info table
     */
    private double pricePerHour;

    /**
     * Constructor for PendingPaymentObjects, param declaration can be found above.
     *
     * @param id
     *              id of the order from the MySql Database
     * @param bikeid
     *              bike id of the order from the MySql Database
     * @param startRent
     *              Start rent date of the order from the MySql Database
     * @param endRent
     *              End rent of the order from the MySql Database
     * @param amount
     *              Total cost of the order from the MySql Database
     * @param amountPayed
     *              Amount already payed of the order from the MySql Database
     * @param type
     *              Bike type of the order from the MySql Database
     * @param pricePerHour
     *              Cost per hour of the order from the MySql Database
     */
    public PendingPaymentObject(int id, int bikeid, Date startRent, Date endRent, double amount, double amountPayed, int type, double pricePerHour){
        this.id = id;
        this.bikeid = bikeid;
        this.startRent = startRent;
        this.endRent = endRent;
        this.amount = amount;
        this.amountPayed = amountPayed;
        this.type = type;
        this.pricePerHour = pricePerHour;
    }

    public ArrayList getTimeRented(){
        long totalTime = endRent.getTime() - startRent.getTime();
        long diffSeconds = totalTime / 1000 % 60;
        long diffMinutes = totalTime / (60 * 1000) % 60;
        long diffHours = totalTime / (60 * 60 * 1000) % 24;
        long diffDays = totalTime / (24 * 60 * 60 * 1000);
        ArrayList<Long> list = new ArrayList<>();
        list.add(0,diffSeconds);
        list.add(1,diffMinutes);
        list.add(2,diffHours);
        list.add(3,diffDays);
        return list;
    }

    /**
     * Getters for the different attributes.
     */
    public int getId() {
        return id;
    }

    public int getBikeid() {
        return bikeid;
    }

    public Date getStartRent() {
        return startRent;
    }

    public Date getEndRent() {
        return endRent;
    }

    public double getAmount() {
        return amount;
    }

    public double getAmountPayed() {
        return amountPayed;
    }

    public int getType() {
        return type;
    }

    public double getPricePerHour() {
        return pricePerHour;
    }
}
