package com.jmc.mazebank.Controllers.Client;

import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Views.ClientMenuOptions;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;

import java.net.URL;
import java.util.ResourceBundle;

public class ClientMenuController implements Initializable {
    public Button dashboard_btn;
    public Button transaction_btn;
    public Button accounts_btn;
    public Button profile_btn;
    public Button logout_btn;
    public Button report_btn;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        addListeners();
    }

    private void addListeners(){
        dashboard_btn.setOnAction(e->onDashboard());
        transaction_btn.setOnAction(e->onTransactions());
        accounts_btn.setOnAction(e->onAccounts());
    }

    private void onTransactions(){ Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.TRANSACTION);}

    private void onAccounts(){ Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.ACCOUNTS);}

    private void onDashboard(){ Model.getInstance().getViewFactory().getClientSelectedMenuItem().set(ClientMenuOptions.DASHBOARD);}
}
