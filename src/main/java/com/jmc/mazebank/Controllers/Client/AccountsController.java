package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.Model;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class AccountsController implements Initializable {
    public Label ch_acc_num;
    public Label ch_date;
    public Label ch_acc_bal;
    public Label sv_acc_num;
    public Label sv_acc_date;
    public Label sv_acc_bal;
    public TextField amount_to_sv;
    public Button trans_to_sv_btn;
    public TextField amount_to_ch;
    public Button trans_to_ch_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        trans_to_sv_btn.setOnAction(e -> {
            Model.getInstance().getDatabaseDriver().moveFundsOnAccount(Model.getInstance().getClient().pAddressProperty().get(), Double.parseDouble(amount_to_sv.getText()), "checking");
            updateUI();
            emptyFields();
        });
        trans_to_ch_btn.setOnAction(e -> {
            Model.getInstance().getDatabaseDriver().moveFundsOnAccount(Model.getInstance().getClient().pAddressProperty().get(), Double.parseDouble(amount_to_ch.getText()), "savings");
            updateUI();
            emptyFields();
        });
    }

    public void bindData() {
        ch_acc_num.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty());
        sv_acc_num.textProperty().bind(Model.getInstance().getClient().savingsAccountProperty().get().accountNumberProperty());
        ch_acc_bal.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString());
        sv_acc_bal.textProperty().bind(Model.getInstance().getClient().savingsAccountProperty().get().balanceProperty().asString());
        ch_date.textProperty().bind(Model.getInstance().getClient().dateProperty().asString());
        sv_acc_date.textProperty().bind(Model.getInstance().getClient().dateProperty().asString());
    }

    public void updateUI() {Model.getInstance().updateBalance();}

    public void emptyFields(){
        amount_to_ch.setText("");
        amount_to_sv.setText("");
    }
}
