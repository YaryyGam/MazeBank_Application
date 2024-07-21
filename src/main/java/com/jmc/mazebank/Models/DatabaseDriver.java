package com.jmc.mazebank.Models;

import java.sql.*;
import java.time.LocalDate;

public class DatabaseDriver {
    private Connection conn;

    public DatabaseDriver(){
        try{
            this.conn = DriverManager.getConnection("jdbc:sqlite:mazebank.db");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /* Client Section */

    public ResultSet getClientData(String pAddress, String password) {
        ResultSet resultSet = null;
        try {
            String query = "SELECT * FROM Clients WHERE PayeeAddress = ? AND Password = ?";
            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setString(1, pAddress);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    /* Admin Section */

    public ResultSet getAdminData(String username, String password){
        ResultSet resultSet = null;
        try{
            String query = "SELECT * FROM Admins WHERE Username = ? AND Password =?";
            PreparedStatement preparedStatement = this.conn.prepareStatement(query);
            preparedStatement.setString(1, username);
            preparedStatement.setString(2, password);
            resultSet = preparedStatement.executeQuery();
        }catch(SQLException e){
            e.printStackTrace();
        }
        return resultSet;
    }

    public void createClient(String fName, String lName, String pAddress, String password, LocalDate date){
        Statement statement;
        try{
            statement = this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                   "Clients (FirstName, LastName, PayeeAddress, Password, Date)"+
                    " VALUES ('"+fName+"', '"+lName+"', '"+pAddress+"', '"+password+"', '"+date.toString()+"');");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createCheckingAccount(String owner, String accountNumber, double transactionLimit, double balance){
        Statement statement;
        try{
            statement=this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    "CheckingAccounts (Owner, AccountNumber, TransactionLimit, Balance)"+
                    " VALUES ('"+owner+"','"+accountNumber+"',"+transactionLimit+","+balance+");");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void createSavingsAccount(String owner, String accountNumber, double withdrawLimit, double balance){
        Statement statement;
        try{
            statement=this.conn.createStatement();
            statement.executeUpdate("INSERT INTO " +
                    "SavingsAccounts (Owner, AccountNumber, WithdrawalLimit, Balance)"+
                    "VALUES ('"+owner+"', '"+accountNumber+"', "+withdrawLimit+", "+balance+");");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /* Utility Method */

    public int getLastClientsID(){
        Statement statement;
        ResultSet resultSet;
        int id = 0;
        try{
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM sqlite_sequence WHERE name='Clients';");
            id=resultSet.getInt("seq");
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return id;
    }
}
