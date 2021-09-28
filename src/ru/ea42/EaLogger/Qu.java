package ru.ea42.EaLogger;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;

public class Qu {
    public PreparedStatement stmt;
    public ResultSet rs;
    private String Text;
    private boolean Hooked;
    private Connection con;
    private int countParam;
    private boolean sel = false;

    // ======================================================================================
    public Qu() {
        Hooked = false;
        countParam = 0;
        String dbURL = "jdbc:postgresql://" + App.DBHost + ":" + App.DBPort + "/" + App.DBDB;
        String driver = "org.postgresql.Driver";
        try {
            Class.forName(driver);
        } catch (ClassNotFoundException ex) {
            App.Log("Driver JDBC not found " + ex);
        }
        try {
            con = DriverManager.getConnection(dbURL, App.DBUser, App.DBPassword);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public boolean hook() {
        // ...
        Hooked = true;
        return true;
    }

    public void release() {
        Text = "";
        rs = null;
        stmt = null;
        countParam = 0;
        Hooked = false;
        sel = false;
    }

    // ======================================================================================
    public void execQu() {
        sel = false;
        try {
            rs = stmt.executeQuery();
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public void exec() {
        sel = false;
        try {
            stmt.execute();
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public ResultSet getResultSet() {
        execQu();
        return rs;
    }

    // ======================================================================================
    public boolean getFirst() {
        execQu();
        sel = true;
        try {
            return rs.next();
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
        return false;
    }

    public boolean getNext() {
        if (!sel) {
            boolean bo = getFirst();
            sel = true;
            return bo;
        }

        try {
            return rs.next();
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
        return false;
    }

    // ======================================================================================
    public void setText(String Text) {
        this.Text = Text;
        try {
            countParam = 0;
            rs = null;
            stmt = null;
            sel = false;
            stmt = con.prepareStatement(Text);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public void setParamString(String param) {
        countParam = countParam + 1;
        try {
            stmt.setString(countParam, param);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public void setParamInt(Integer param) {
        countParam = countParam + 1;
        try {
            stmt.setInt(countParam, param);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public void setParamTimestamp(Timestamp param) {
        countParam = countParam + 1;
        try {
            stmt.setTimestamp(countParam, param);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    public void setParamBytes(byte[] param) {
        countParam = countParam + 1;
        try {
            stmt.setBytes(countParam, param);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
    }

    // ======================================================================================
    public String asString(String FildName) {
        String strRet = "";
        if (!sel) {
            if (!getFirst()) {
                return strRet;
            }
        }

        try {
            strRet = rs.getString(FildName);
        } catch (Exception ex) {
            App.Log(ex.toString());
        }
        return strRet;
    }

    // ======================================================================================

}