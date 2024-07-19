package com.jmc.mazebank.Models;

import java.sql.*;

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


    /* Admin Section */

    /* Utility Method */
}
