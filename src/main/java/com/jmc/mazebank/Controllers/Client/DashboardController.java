package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Views.TransactionCellFactory;
import javafx.beans.binding.Bindings;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.text.Text;

import java.net.URL;
import java.time.LocalDate;
import java.util.Locale;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {
    public Label login_date;
    public Text user_name;
    public Label cheking_balance;
    public Label cheking_acc_num;
    public Label savings_bal;
    public Label savings_acc_num;
    public Label income_lbl;
    public Label expense_lbl;
    public ListView transaction_listview;
    public TextField payee_fld;
    public TextField amount_fld;
    public TextArea message_fld;
    public Button send_money_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        bindData();
        initLatestTransactionsList();
        transaction_listview.setItems(Model.getInstance().getLatestTransactions());
        transaction_listview.setCellFactory(e->new TransactionCellFactory());
    }

    private void bindData(){
        user_name.textProperty().bind(Bindings.concat("Hi, ").concat(Model.getInstance().getClient().firstNameProperty()));
        login_date.setText("Today, " + LocalDate.now());
        cheking_balance.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().balanceProperty().asString());
        cheking_acc_num.textProperty().bind(Model.getInstance().getClient().checkingAccountProperty().get().accountNumberProperty());
        savings_bal.textProperty().bind(Model.getInstance().getClient().savingsAccountProperty().get().balanceProperty().asString());
        savings_acc_num.textProperty().bind(Model.getInstance().getClient().savingsAccountProperty().get().accountNumberProperty());
    }

    private void initLatestTransactionsList(){
        if(Model.getInstance().getLatestTransactions().isEmpty()){
            Model.getInstance().setLatestTransactions();
        }
    }
}
