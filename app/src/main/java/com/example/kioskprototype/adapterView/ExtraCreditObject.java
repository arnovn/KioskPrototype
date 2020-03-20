package com.example.kioskprototype.adapterView;

import java.io.Serializable;
import java.util.Date;

public class ExtraCreditObject implements Serializable {

    private Date timeOrdered;

    private double amount;

    public ExtraCreditObject(Date timeOrdered, double amount){
        this.timeOrdered = timeOrdered;
        this.amount = amount;
    }

    public Date getTimeOrdered() {
        return timeOrdered;
    }

    public double getAmount() {
        return amount;
    }
}
