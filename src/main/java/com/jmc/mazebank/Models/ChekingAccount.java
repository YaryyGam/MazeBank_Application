package com.jmc.mazebank.Models;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class ChekingAccount extends Account{
    // The number of transactions that client  can do per day
    private final IntegerProperty transactionLimit;

    public ChekingAccount(String owner, String accountNumber, double balance, int transactionLimit){
        super(owner, accountNumber, balance);
        this.transactionLimit = new SimpleIntegerProperty(this, "Transaction Limit", transactionLimit);
    }

    public IntegerProperty transactionLimitProperty(){return transactionLimit;}

    @Override
    public String toString(){
        return accountNumberProperty().get();
    }
}
