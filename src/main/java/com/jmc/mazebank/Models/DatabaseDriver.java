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

    public double getCheckingAccountBalance(String pAddress){
        PreparedStatement statement = null;
        ResultSet resultSet;
        double balance = 0;
        try {
            String sql ="SELECT * FROM CheckingAccounts WHERE Owner=?";
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

    public void moveFundsOnAccount(String pAddress, double amount, String operation){
        PreparedStatement selectStatementChecking = null;
        PreparedStatement selectStatementSaving = null;
        PreparedStatement updateCheckingStatement = null;
        PreparedStatement updateSavingStatement = null;
        ResultSet resultSetChecking;
        ResultSet resultSetSavings;

        try {
            String sqlSelectChecking = "SELECT * FROM CheckingAccounts WHERE Owner=?";
            String sqlSelectSavings = "SELECT * FROM SavingsAccounts WHERE Owner=?";
            String sqlUpdateChecking = "UPDATE CheckingAccounts SET Balance=? WHERE Owner=?";
            String sqlUpdateSavings = "UPDATE SavingsAccounts SET Balance=? WHERE Owner=?";

            double newBalance;
            double updatedBalance;

            selectStatementChecking = this.conn.prepareStatement(sqlSelectChecking);
            selectStatementSaving = this.conn.prepareStatement(sqlSelectSavings);
            selectStatementChecking.setString(1, pAddress);
            selectStatementSaving.setString(1, pAddress);

            resultSetChecking = selectStatementChecking.executeQuery();
            resultSetSavings = selectStatementSaving.executeQuery();

            if(operation.equalsIgnoreCase("savings")){
                if(resultSetSavings.getDouble("Balance")>=amount){
                    newBalance = resultSetSavings.getDouble("Balance") - amount;
                    updateSavingStatement = this.conn.prepareStatement(sqlUpdateSavings);
                    updateSavingStatement.setDouble(1, newBalance);
                    updateSavingStatement.setString(2, pAddress);
                    updateSavingStatement.executeUpdate();
                    updatedBalance = resultSetChecking.getDouble("Balance") + amount;
                    updateCheckingStatement = this.conn.prepareStatement(sqlUpdateChecking);
                    updateCheckingStatement.setDouble(1, updatedBalance);
                    updateCheckingStatement.setString(2, pAddress);
                    updateCheckingStatement.executeUpdate();
                }
            }else{
                if(resultSetChecking.getDouble("Balance")>=amount){
                    newBalance = resultSetChecking.getDouble("Balance") - amount;
                    updateCheckingStatement = this.conn.prepareStatement(sqlUpdateChecking);
                    updateCheckingStatement.setDouble(1, newBalance);
                    updateCheckingStatement.setString(2, pAddress);
                    updateCheckingStatement.executeUpdate();
                    updatedBalance = resultSetSavings.getDouble("Balance") + amount;
                    updateSavingStatement = this.conn.prepareStatement(sqlUpdateSavings);
                    updateSavingStatement.setDouble(1, updatedBalance);
                    updateSavingStatement.setString(2, pAddress);
                    updateSavingStatement.executeUpdate();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }

    }

    public void sendReport(String pAddress, String message){
        PreparedStatement preparedStatement = null;
        try {
            String sql = "INSERT INTO Reports (PayeeAddress, ReportText) VALUES (?, ?)";
            preparedStatement = this.conn.prepareStatement(sql);
            preparedStatement.setString(1, pAddress);
            preparedStatement.setString(2, message);
            preparedStatement.executeUpdate();
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public void AmountOfTransactions(String pAddress) {
        PreparedStatement selectStatement = null;
        PreparedStatement insertStatement = null;
        PreparedStatement updateStatement = null;
        ResultSet resultSet;

        try {
            // Запити для вибірки, вставки та оновлення
            String selectTransactionCountSql = "SELECT COUNT(*) AS transaction_count FROM Transactions WHERE Sender=? OR Receiver=?";
            String selectAddressSql = "SELECT COUNT(*) AS address_count FROM TransactionsAmount WHERE PayeeAddress=?";
            String insertSql = "INSERT INTO TransactionsAmount (PayeeAddress, TransAmount) VALUES (?, ?)";
            String updateSql = "UPDATE TransactionsAmount SET TransAmount=? WHERE PayeeAddress=?";

            // Обчислити кількість транзакцій
            selectStatement = this.conn.prepareStatement(selectTransactionCountSql);
            selectStatement.setString(1, pAddress);
            selectStatement.setString(2, pAddress);

            resultSet = selectStatement.executeQuery();
            int amountOfTransactions = 0;
            if (resultSet.next()) {
                amountOfTransactions = resultSet.getInt("transaction_count");
            }

            // Перевірити, чи існує адреса у таблиці TransactionsAmount
            selectStatement = this.conn.prepareStatement(selectAddressSql);
            selectStatement.setString(1, pAddress);
            resultSet = selectStatement.executeQuery();

            if (resultSet.next()) {
                int addressCount = resultSet.getInt("address_count");
                if (addressCount > 0) {
                    // Оновити кількість транзакцій
                    updateStatement = this.conn.prepareStatement(updateSql);
                    updateStatement.setInt(1, amountOfTransactions);
                    updateStatement.setString(2, pAddress);
                    updateStatement.executeUpdate();
                } else {
                    // Вставити новий запис
                    insertStatement = this.conn.prepareStatement(insertSql);
                    insertStatement.setString(1, pAddress);
                    insertStatement.setInt(2, amountOfTransactions);
                    insertStatement.executeUpdate();
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }
    }

    public int getAmountOfTransactions(String pAddress){
        AmountOfTransactions(pAddress);
        int amountOfTransactions = 0;
        PreparedStatement statement = null;
        ResultSet resultSet;

        try{
            String sql = "SELECT * FROM TransactionsAmount WHERE PayeeAddress=?";
            statement = this.conn.prepareStatement(sql);
            statement.setString(1, pAddress);
            resultSet = statement.executeQuery();
            amountOfTransactions = resultSet.getInt("TransAmount");
        }catch (SQLException e){
            e.printStackTrace();
        }


        return amountOfTransactions;
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

    public void deleteUserMethod(String pAddress){
        PreparedStatement selectStatementClients = null;
        PreparedStatement selectStatement_CH_acc = null;
        PreparedStatement selectStatement_SV_acc = null;
        PreparedStatement deleteStatementClients = null;
        PreparedStatement deleteStatement_CH_acc = null;
        PreparedStatement deleteStatement_SV_acc = null;
        ResultSet resultSetClients = null;
        ResultSet resultSetCH_acc = null;
        ResultSet resultSetSV_acc = null;

        try {
            //Select user method
            String selectSQLClients = "SELECT * FROM Clients WHERE PayeeAddress=?";
            String selectSQL_CH_acc = "SELECT * FROM CheckingAccounts WHERE Owner=?";
            String selectSQL_SV_acc = "SELECT * FROM SavingsAccounts WHERE Owner=?";
            //Delete user method
            String deleteSQLClients = "DELETE FROM Clients WHERE PayeeAddress=?";
            String deleteSQL_CH_acc = "DELETE FROM CheckingAccounts WHERE Owner=?";
            String deleteSQL_SV_acc = "DELETE FROM SavingsAccounts WHERE Owner=?";
            //Preparing statements
            //Select statement
            selectStatementClients = this.conn.prepareStatement(selectSQLClients);
            selectStatement_CH_acc = this.conn.prepareStatement(selectSQL_CH_acc);
            selectStatement_SV_acc = this.conn.prepareStatement(selectSQL_SV_acc);
            //Delete statement
            deleteStatementClients = this.conn.prepareStatement(deleteSQLClients);
            deleteStatement_CH_acc = this.conn.prepareStatement(deleteSQL_CH_acc);
            deleteStatement_SV_acc = this.conn.prepareStatement(deleteSQL_SV_acc);

            selectStatementClients.setString(1, pAddress);
            selectStatement_CH_acc.setString(1, pAddress);
            selectStatement_SV_acc.setString(1, pAddress);

            deleteStatementClients.setString(1, pAddress);
            deleteStatement_CH_acc.setString(1, pAddress);
            deleteStatement_SV_acc.setString(1, pAddress);

            resultSetClients = selectStatementClients.executeQuery();
            resultSetCH_acc = selectStatement_CH_acc.executeQuery();
            resultSetSV_acc = selectStatement_SV_acc.executeQuery();

            //If exists, delete
            if (resultSetClients.next()) {
                deleteStatementClients.executeUpdate();
            }
            if (resultSetCH_acc.next()) {
                deleteStatement_CH_acc.executeUpdate();
            }
            if (resultSetSV_acc.next()) {
                deleteStatement_SV_acc.executeUpdate();
            }


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
