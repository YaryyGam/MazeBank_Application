package com.jmc.mazebank.Models;

import javafx.beans.property.*;

import java.time.LocalDate;

public class Client {

    private final StringProperty firstName;
    private final StringProperty lastName;
    private final StringProperty payeeAdress;
    private final ObjectProperty<Account> checkingAccount;
    private final ObjectProperty<Account> savingsAccount;
    private final ObjectProperty<LocalDate> dateCreated;
    private final IntegerProperty transactionLimit;
    private final DoubleProperty withdrawalLimit;

    public Client(String fName, String lName, String pAddress, Account cAccount, Account sAccount, LocalDate date){
        this.firstName = new SimpleStringProperty(this, "FirstName", fName);
        this.lastName = new SimpleStringProperty(this, "LastName", lName);
        this.payeeAdress = new SimpleStringProperty(this, "Payee Address", pAddress);
        this.checkingAccount = new SimpleObjectProperty<>(this, "Checking Account", cAccount);
        this.savingsAccount =new SimpleObjectProperty<>(this, "Savings Account", sAccount);
        this.dateCreated =new SimpleObjectProperty<>(this, "Date", date);
        this.transactionLimit = new SimpleIntegerProperty(this, "Transaction Limit", 10);
        this.withdrawalLimit = new SimpleDoubleProperty(this, "Withdrawal Limit", 2000.00);
    }

    public StringProperty firstNameProperty(){return firstName;}
    public StringProperty lastNameProperty(){return lastName;}
    public StringProperty pAddressProperty(){return payeeAdress;}
    public ObjectProperty<Account> checkingAccountProperty(){return checkingAccount;}
    public ObjectProperty<Account> savingsAccountProperty(){return savingsAccount;}
    public ObjectProperty<LocalDate> dateProperty(){return dateCreated;}
    public IntegerProperty transactionLimitProperty(){return transactionLimit;}
    public DoubleProperty withdrawalLimitProperty(){return withdrawalLimit;}
}
