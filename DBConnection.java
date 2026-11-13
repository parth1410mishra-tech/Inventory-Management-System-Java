package com.parth.inventory;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
public final class DBConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/retail_db";
    private static final String USER = "root";
    // Replace with your local MySQL password. Never commit a real password.
    private static final String PASSWORD = "YOUR_MYSQL_PASSWORD";
    private DBConnection() {}
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL, USER, PASSWORD);
    }
}