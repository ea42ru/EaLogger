package ru.ea42.EaLogger;

import com.google.gson.JsonObject;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class QuManager {
    public boolean testFail() {
        // TODO Подключение к ДБ и создание при необходимости
        String dbURL = "jdbc:postgresql://" + App.DBHost + ":" + App.DBPort
                + "/" + App.DBDB;
        String driver = "org.postgresql.Driver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            App.Log("Driver JDBC not found " + ex);
            return true;
        }
        try {
            Connection con = DriverManager.getConnection(dbURL, App.DBUser,
                    App.DBPassword);
            Statement stmt = con.createStatement();
            String SQL = "SELECT log FROM registr LIMIT 1";
            ResultSet rs = stmt.executeQuery(SQL);
            rs.next();
        } catch (SQLException ex) {
            App.Log(ex.toString());
            return true;
        }
        return false;
    }

    public Qu getQu(String Text) {
        Qu Qu = getQu();
        Qu.setText(Text);
        return Qu;
    }

    public Qu getQu() {
        Qu Qu = new Qu();
        Qu.setText("");
        return Qu;
    }

    // получить help
    public String getHelp() {
        InputStream jsFS;
        String jsStr = "";
        try {
            jsFS = this.getClass().getClassLoader().getResourceAsStream("аpi.txt");
            if (jsFS == null)
                jsFS = new FileInputStream("аpi.txt");

            if (jsFS != null) {
                int ch;
                ByteArrayOutputStream sb = new ByteArrayOutputStream();
                while ((ch = jsFS.read()) != -1)
                    sb.write(ch);
                jsStr = sb.toString("UTF8");
                jsFS.close();
            }
        } catch (IOException e) {
        }

        if (jsStr == "") jsStr = App.AppVersion;
        JsonObject jsResp = new JsonObject();
        jsResp.addProperty("Type", "Help");
        jsResp.addProperty("Val", jsStr);
        return jsResp.toString();
    }
}