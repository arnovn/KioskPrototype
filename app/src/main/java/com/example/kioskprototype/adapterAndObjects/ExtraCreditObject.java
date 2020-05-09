package com.example.kioskprototype.adapterAndObjects;

import java.io.Serializable;
import java.util.Date;

/**
 * Class which transforms the amount of extra credit a user likes to add to his account to ExtraCreditObjects
 */
public class ExtraCreditObject implements Serializable {

    /**
     * Date when the extra credits were added.
     */
    private Date timeOrdered;

    /**
     * The amount of extra credits that were added.
     */
    private double amount;

    /**
     * ExtraCreditObject constructor
     * @param timeOrdered
     *          Time at which the user added the credits
     * @param amount
     *          Amount of credits added.
     */
    public ExtraCreditObject(Date timeOrdered, double amount){
        this.timeOrdered = timeOrdered;
        this.amount = amount;
    }

    /**
     * Getter for when the credits were added.
     * @return
     *          Time when credits were added.
     */
    public Date getTimeOrdered() {
        return timeOrdered;
    }

    /**
     * Getter for the amount of credits added.
     * @return
     *          Amount of credits added.
     */
    public double getAmount() {
        return amount;
    }
}
