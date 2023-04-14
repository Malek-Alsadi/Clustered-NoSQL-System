package com.atypon.demo.Objects;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Customers extends Persons{
    private double Balance;

    public Customers() {
    }
    public Customers(String Id, String name, double balance) {

        super.setId(Id);
        super.setName(name);
        Balance = balance;
    }
    @JsonProperty("Balance")
    public double getBalance() {
        return Balance;
    }

    public void setBalance(double balance) {
        Balance = balance;
    }

}
