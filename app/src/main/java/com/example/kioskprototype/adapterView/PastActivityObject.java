package com.example.kioskprototype.adapterView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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
        ArrayList<Long> durations = getTimeRented();
        return convertTimeToString(durations);
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
