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

    public ResultSet getTransactions(String pAddress, int limit) {
        PreparedStatement statement = null;
        ResultSet resultSet = null;
        try {
            String sql = "SELECT * FROM Transactions WHERE Sender=? OR Receiver=? LIMIT ?";
            statement = this.conn.prepareStatement(sql);
            statement.setString(1, pAddress);
            statement.setString(2, pAddress);
            statement.setInt(3, limit);
            resultSet = statement.executeQuery();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    // Method returns savings account balance
    public double getSavingsAccountBalance(String pAddress){
        PreparedStatement statement = null;
        ResultSet resultSet;
        double balance = 0;
        try {
            String sql ="SELECT * FROM SavingsAccounts WHERE Owner=?";
            statement = this.conn.prepareStatement(sql);
            statement.setString(1, pAddress);
            resultSet = statement.executeQuery();
            balance = resultSet.getDouble("Balance");
        }catch (SQLException e){
            e.printStackTrace();
        }
        return balance;
    }

    // Method to either add or subtract from balance given operation
    public void updateBalance(String pAddress, double amount, String operation){
        PreparedStatement selectStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet;
        try {
            String sql = "SELECT * FROM SavingsAccounts WHERE Owner=?";
            String sqlUpdate = "UPDATE SavingsAccounts SET Balance=? WHERE Owner=?";
            double newBalance;
            selectStatement = this.conn.prepareStatement(sql);
            selectStatement.setString(1, pAddress);
            resultSet = selectStatement.executeQuery();
            if(operation.equals("ADD")){
                newBalance = resultSet.getDouble("Balance") + amount;
                updateStatement = this.conn.prepareStatement(sqlUpdate);
                updateStatement.setDouble(1,newBalance);
                updateStatement.setString(2,pAddress);
                updateStatement.executeUpdate();
            }else{
                if(resultSet.getDouble("Balance")>= amount){
                    newBalance = resultSet.getDouble("Balance") - amount;
                    updateStatement = this.conn.prepareStatement(sqlUpdate);
                    updateStatement.setDouble(1,newBalance);
                    updateStatement.setString(2,pAddress);
                    updateStatement.executeUpdate();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    // Creates and records new transaction
    public void newTransaction(String sender, String receiver, double amount, String message){
        PreparedStatement insertStatement = null;
        try {
            LocalDate date = LocalDate.now();
            String sql = "INSERT INTO Transactions (Sender, Receiver, Amount, Date, Message) VALUES (?, ?, ?, ?, ?)";
            insertStatement = this.conn.prepareStatement(sql);
            insertStatement.setString(1, sender);
            insertStatement.setString(2, receiver);
            insertStatement.setDouble(3, amount);
            insertStatement.setString(4, date.toString());
            insertStatement.setString(5, message);
            insertStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
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

    public ResultSet getAllClientsData(){
        Statement statement;
        ResultSet resultSet=null;
        try {
            statement=this.conn.createStatement();
            resultSet=statement.executeQuery("SELECT * FROM Clients;");
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public void depositSavings(String pAddress, double amount){
        Statement statement;
        try {
            statement = this.conn.createStatement();
            statement.executeUpdate("UPDATE SavingsAccounts SET Balance="+amount+" WHERE Owner='"+pAddress+"';");
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    /* Utility Method */

    public ResultSet searchClient(String pAddress){
        Statement statement;
        ResultSet resultSet =null;
        try {
            statement = this.conn.createStatement();
            resultSet = statement.executeQuery("SELECT * FROM Clients WHERE PayeeAddress='"+pAddress+"';");
        }catch (Exception e){
            e.printStackTrace();
        }
        return resultSet;
    }

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

    public ResultSet getCheckingAccountData(String pAddress){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement=this.conn.createStatement();
            resultSet=statement.executeQuery("SELECT * FROM CheckingAccounts WHERE Owner='"+pAddress+"';");
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }

    public ResultSet getSavingsAccountData(String pAddress){
        Statement statement;
        ResultSet resultSet = null;
        try {
            statement=this.conn.createStatement();
            resultSet=statement.executeQuery("SELECT * FROM SavingsAccounts WHERE Owner='"+pAddress+"';");
        }catch (SQLException e) {
            e.printStackTrace();
        }
        return resultSet;
    }
}
