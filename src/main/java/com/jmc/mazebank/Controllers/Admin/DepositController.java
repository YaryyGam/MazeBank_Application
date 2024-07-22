package com.jmc.mazebank.Controllers.Admin;

import com.jmc.mazebank.Models.Client;
import com.jmc.mazebank.Models.Model;
import com.jmc.mazebank.Views.ClientCellFactory;
import javafx.collections.ObservableList;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class DepositController implements Initializable {


    public TextField pAdress_fld;
    public Button search_btn;
    public ListView<Client> results_listview;
    public TextField amount_fld;
    public Button deposit_btn;

    private Client client;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        search_btn.setOnAction(e->onClientSearch());
        deposit_btn.setOnAction(e->onDeposit());
    }

    private void onClientSearch(){
        ObservableList<Client> searchResults = Model.getInstance().searchClient(pAdress_fld.getText());
        results_listview.setItems(searchResults);
        results_listview.setCellFactory(e->new ClientCellFactory());
        client = searchResults.get(0);
    }

    private void onDeposit(){
        double amount = Double.parseDouble(amount_fld.getText());
        double newBalance = amount + client.savingsAccountProperty().get().balanceProperty().get();
        if(amount_fld.getText() != null){
            Model.getInstance().getDatabaseDriver().depositSavings(client.pAddressProperty().get(), newBalance);
        }
        emptyFields();
    }

    private void emptyFields(){
        pAdress_fld.setText("");
        amount_fld.setText("");
    }
}
