package com.myProject.twitch_recommendation.db;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class MySQLTableCreator {

    // run this separately as a java application to reset the database
    public static void main(String[] args) {
        try {
            // 1. connect to MySQL
            System.out.println("Connecting to " + MySQLDBUtil.getMySQLAddress());
            // required by the jdbc library
            Class.forName("com.mysql.cj.jdbc.Driver").newInstance();
            Connection conn = DriverManager.getConnection(MySQLDBUtil.getMySQLAddress());

            if (conn == null) {
                return;
            }

            // 2. if the tables already exist, drop them
            Statement statement = conn.createStatement();
            // drop the table that has foreign key first
            String sql = "DROP TABLE IF EXISTS favorite_records";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS items";
            statement.executeUpdate(sql);

            sql = "DROP TABLE IF EXISTS users";
            statement.executeUpdate(sql);

            // 3. create new tables
            sql = "CREATE TABLE items ("
                    + "id VARCHAR(255) NOT NULL,"
                    + "title VARCHAR(255),"
                    + "url VARCHAR(255),"
                    + "thumbnail_url VARCHAR(255),"
                    + "broadcaster_name VARCHAR(255),"
                    + "game_id VARCHAR(255),"
                    + "type VARCHAR(255) NOT NULL,"
                    + "PRIMARY KEY (id)"
                    + ")";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE users ("
                    + "id VARCHAR(255) NOT NULL,"
                    + "password VARCHAR(255) NOT NULL,"
                    + "first_name VARCHAR(255),"
                    + "last_name VARCHAR(255),"
                    + "PRIMARY KEY (id)"
                    + ")";
            statement.executeUpdate(sql);

            sql = "CREATE TABLE favorite_records ("
                    + "user_id VARCHAR(255) NOT NULL,"
                    + "item_id VARCHAR(255) NOT NULL,"
                    + "last_favor_time TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,"
                    + "PRIMARY KEY (user_id, item_id),"
                    + "FOREIGN KEY (user_id) REFERENCES users(id),"
                    + "FOREIGN KEY (item_id) REFERENCES items(id)"
                    + ")";
            statement.executeUpdate(sql);

            conn.close();
            System.out.println("Import done successfully");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
