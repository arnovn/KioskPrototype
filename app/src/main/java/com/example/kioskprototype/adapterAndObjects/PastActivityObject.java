package com.example.kioskprototype.adapterAndObjects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PastActivityObject implements Serializable {

    /**
     * id of the bike
     */
    private int bikeId;

    /**
     * date of ordering (Date = dd-MM-yyyy hh-mm-ss)
     *  - If at kiosk: when order finished at Kiosk
     *  - If at bike: orderDate = startRent
     */
    private Date orderDate;

    /**
     * Date when rent started
     */
    private Date startRent;

    /**
     * Date when rent ended
     */
    private Date endRent;

    /**
     * Total cost of the rent
     */
    private double amount;

    /**
     * Amount that has already been payed for that order
     */
    private double amountpayed;

    /**
     * Constructor of PastActivityObject
     * @param bikeId
     *              Id of the bike inside the MySql Database.
     * @param orderDate
     *              OrderDate from the MySql Database.
     * @param startRent
     *              Start rent date from the MySql Database.
     * @param endRent
     *              End rent date from the MySql Database.
     * @param amount
     *              Total cost of the rent from MySql Database.
     * @param amountpayed
     *              What already has been payed by the user from MySql Database.
     */
    public PastActivityObject(int bikeId, Date orderDate, Date startRent, Date endRent, double amount, double amountpayed){
        this.bikeId = bikeId;
        this.orderDate = orderDate;
        this.startRent = startRent;
        this.endRent = endRent;
        this.amount = amount;
        this.amountpayed = amountpayed;
    }

    /**
     * Getter for the name of the bike from the order.
     * @return
     *          The name of the bike (e.g. Bike12)
     */
    public String getBikeName(){
        return "Bike"+bikeId;
    }

    /**
     * Getter for the duration of the rent
     * @return
     *          The duration of the rent.
     */
    public String getDuration(){
        ArrayList durations = getTimeRented();
        return convertTimeToString(durations);
    }

    /**
     * Getter for the id of the bike of the order.
     * @return
     *          The id of the bike
     */
    public int getBikeId() {
        return bikeId;
    }

    /**
     * Getter for the date of the order.
     * @return
     *          The date of the order.
     */
    public Date getOrderDate() {
        return orderDate;
    }

    /**
     * Getter for the start rent date
     * @return
     *          Start rent date
     */
    public Date getStartRent() {
        return startRent;
    }

    /**
     * Getter for the end of the rent
     * @return
     *          End rent date
     */
    public Date getEndRent() {
        return endRent;
    }

    /**
     * Getter for the amount to be payed
     * @return
     *          The amount to be payed for this order
     */
    public double getAmount() {
        return amount;
    }

    /**
     * Getter for the amount that has already been payed
     * @return
     *          The amount that has already been payed by the user for the current order.
     */
    public double getAmountpayed() {
        return amountpayed;
    }

    /**
     * Calculates the difference in time between the start of rend and the end of the rent
     * @return
     *          List consisting the duration of one bike rent
     *              - list[3]: amount of days
     *              - list[2]: remaining hours
     *              - list[1]: remaining minutes
     *              - list[0]: remaining seconds
     */
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
     * Converts the duration list to a string
     * @param timeList
     *              List which has the data for the duration (see getRentedTime())
     * @return
     *              Converted String duration
     */
    public String convertTimeToString(List timeList){
        String time;
        if(!timeList.get(3).toString().equals("0")){
            time = timeList.get(3).toString() + "d " + timeList.get(2) + "h" + timeList.get(1) + "min " + timeList.get(0) + "s";
        }else if(!timeList.get(2).toString().equals("0")){
            time = timeList.get(2) + "h" + timeList.get(1) + "min " + timeList.get(0) + "s";
        }else if(!timeList.get(1).toString().equals("0")){
            time = timeList.get(1) + "min " + timeList.get(0) + "s";
        }else if(!timeList.get(0).toString().equals("0")){
            time = timeList.get(0) + "s";
        }else{
            return "Not used.";
        }
        return time;
    }
}
