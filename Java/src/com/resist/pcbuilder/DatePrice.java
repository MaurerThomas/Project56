package com.resist.pcbuilder;

import java.sql.Date;

/**
 * A wrapper that combines dates and prices.
 *
 * @author Thomas
 */
public class DatePrice {
    private Date date;
    private double price;

    public DatePrice(Date date, double price) {
        this.date = date;
        this.price = price;
    }

    public double getPrice() {
        return price;
    }

    public Date getDate() {
        return date;
    }
}
