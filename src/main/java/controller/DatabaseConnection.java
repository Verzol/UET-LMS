package controller;

import java.sql.Connection;
import java.sql.DriverManager;

public class DatabaseConnection {
    public Connection databaseLink;

    public DatabaseConnection() {
    }

    public Connection getConnection() {
        String databaseName = "quanlysach";
        String databaseUser = "root";
        String databasePassword = "abcd@1234";
        String databaseUrl = "jdbc:mysql://171.244.63.61:2005/" + databaseName;

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.databaseLink = DriverManager.getConnection(databaseUrl, databaseUser, databasePassword);
        } catch (Exception var6) {
            Exception e = var6;
            System.out.println("Error connecting to the database!");
            e.printStackTrace();
        }

        return this.databaseLink;
    }
}