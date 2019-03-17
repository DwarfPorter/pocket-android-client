package com.gb.pocketmessenger.db;

import android.annotation.SuppressLint;
import android.net.Uri;
import android.os.StrictMode;
import android.util.Log;

import java.net.MalformedURLException;
import java.net.URI;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class ConnectionDB {
    // String classs = "com.mysql.jdbc.Driver";
//    String url = "jdbc:mysql://192.168.0.103/chat";
//    String user = "user";
//    String password = "123";


    String classs = "org.postgresql.Driver";
    String url = "jdbc:postgresql://pocketmsg.herokuapp.com:5432/d8tvkcb74gcdm9";
    String user = "iybsnghodfpvsc";
    String password = "4a846f4de9607834e40d7204ddfa013cfd21b2df2881f6b55465decca061adb0";

    @SuppressLint("NewApi")
    public Connection CONN() {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder()
                .permitAll().build();
        StrictMode.setThreadPolicy(policy);
        Connection conn = null;
        try {
            Class.forName(classs);
            conn = DriverManager.getConnection(url, user, password);
        } catch (SQLException se) {
            Log.e("ERRO", se.getMessage());
        } catch (ClassNotFoundException e) {
            Log.e("ERRO", e.getMessage());
        } catch (Exception e) {
            Log.e("ERRO", e.getMessage());
        }
        return conn;
    }
}
