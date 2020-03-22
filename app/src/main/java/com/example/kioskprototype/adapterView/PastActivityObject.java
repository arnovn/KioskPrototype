package com.example.kioskprototype.adapterView;

import java.io.Serializable;
import java.util.Date;

public class PastActivityObject implements Serializable {

    int bikeId;
    Date orderDate;
    Date startRent;
    Date endRent;
    double amount;
    double amountpayed;

    public PastActivityObject(int bikeId, Date orderDate, Date startRent, Date endRent, double amount, double amountpayed){
        this.bikeId = bikeId;
        this.orderDate = orderDate;
        this.startRent = startRent;
        this.endRent = endRent;
        this.amount = amount;
        this.amountpayed = amountpayed;
    }

    public String getBikeName(){
        return "Bike"+bikeId;
    }

    public String getDuration(){
        return "TO DO IMPLEMENT";
    }

    public int getBikeId() {
        return bikeId;
    }

    public Date getOrderDate() {
        return orderDate;
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

    public double getAmountpayed() {
        return amountpayed;
    }
}
