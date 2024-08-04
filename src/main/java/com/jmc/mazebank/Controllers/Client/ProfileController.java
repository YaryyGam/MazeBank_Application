package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.Model;
import javafx.beans.property.SimpleStringProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Label;

import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;

public class ProfileController implements Initializable {
    public Label sv_balance_lbl;
    public Label ch_balance_lbl;
    public Label total_transactions_lbl;
    public Label date_lbl;
    public Label fName_lbl;
    public Label lName_lbl;
    public Label pAddress_lbl;
    public Label sv_acc_status_lbl;
    public Label ch_acc_status_lbl;
    public Label total_balance_lbl;

    private SimpleStringProperty totalTransactions;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        totalTransactions = new SimpleStringProperty();
        bindData();
        checkAccount();
        updateTransactions();
        setupListeners();
    }

    private void bindData(){
        date_lbl.setText(LocalDate.now().toString());
        fName_lbl.setText(Model.getInstance().getClient().firstNameProperty().get());
        lName_lbl.setText(Model.getInstance().getClient().lastNameProperty().get());
        pAddress_lbl.setText(Model.getInstance().getClient().pAddressProperty().get());
    }

    private void checkAccount(){
            ch_acc_status_lbl.setText("Online");
            ch_balance_lbl.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString());
            sv_acc_status_lbl.setText("Online");
            sv_balance_lbl.textProperty().bind(Model.getInstance().getClient().savingsAccountProperty().get().balanceProperty().asString());
            updateTotalBalance();
            total_transactions_lbl.textProperty().bind(totalTransactions);
    }

    private void updateTransactions(){
        int count = Model.getInstance().getDatabaseDriver().getAmountOfTransactions(Model.getInstance().getClient().pAddressProperty().get());
        totalTransactions.set(Integer.toString(count));
    }

    private void updateTotalBalance() {
        double checkingBalance = Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().get();
        double savingsBalance = Model.getInstance().getClient().savingsAccountProperty().get().balanceProperty().get();
        double totalBalance = checkingBalance + savingsBalance;
        total_balance_lbl.setText(String.format("%.2f", totalBalance));
    }

    private void setupListeners() {
        // Слухач для оновлення загального балансу при зміні балансу рахунків
        Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().addListener((observable, oldValue, newValue) -> updateTotalBalance());
        Model.getInstance().getClient().savingsAccountProperty().get().balanceProperty().addListener((observable, oldValue, newValue) -> updateTotalBalance());

        Model.getInstance().getClient().pAddressProperty().addListener((observable, oldValue, newValue) -> {
            updateTransactions();
        });
    }
}
