package ru.ea42.EaLogger;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.sql.Timestamp;
import java.util.Map;

public class HandlerApi01 extends HttpHandlerBased {
    protected String doGet(HttpExchange exchange, Map params) {
        return App.QuMan.getHelp();
    }

    // =============================================
    // маршрутизация запросов
    protected String doPost(HttpExchange exchange, Map params, String body) {
        // запрос JSON?
        JsonObject jsRoot;
        String rType;
        try {
            jsRoot = new JsonParser().parse(body).getAsJsonObject();
            rType = jsRoot.get("Type").getAsString();
        } catch (Exception ex) {
            return postErr("101001", "Request not JSON", body);
        }

        // версия АПИ, пинг
        if (rType.equals("Version"))
            return "{\"Type\": \"API\", \"Data\": \"api01\"}";

        // логирование
        if (rType.equals("quLog"))
            return quLog(jsRoot);

        return postErr("101002", "Unknown request type", body);
    }

    // =============================================
    // Вернуть Ok
    private String postOk(JsonObject jsRoot) {
        return "{\"Type\": \"Ok\"}";
    }

    // Вернуть ошибку
    private String postErr(String num, String mess, String body) {
        JsonObject jsResp = new JsonObject();
        jsResp.addProperty("Type", "Error");
        jsResp.addProperty("Num", num);
        jsResp.addProperty("Mess", mess);
        jsResp.addProperty("Data", body);
        return jsResp.toString();
    }

    // логирование
    private String quLog(JsonObject jsRoot) {
        Qu qu = App.QuMan.getQu("INSERT INTO log (times, level, sys, computer, \"user\", id, mess) VALUES (?, ?, ?, ?, ?, ?, ?)");
        qu.setParamTimestamp(new Timestamp(System.currentTimeMillis()));
        qu.setParamInt(jsRoot.get("Level").getAsInt());
        qu.setParamString(jsRoot.get("Sys").getAsString());
        qu.setParamString(jsRoot.get("Computer").getAsString());
        qu.setParamString(jsRoot.get("User").getAsString());
        qu.setParamString(jsRoot.get("Id").getAsString());
        qu.setParamString(jsRoot.get("Mess").getAsString());
        try {
            qu.exec();
        } catch (Exception ex) {
        }
        qu.release();
        return postOk(jsRoot);
    }
}
