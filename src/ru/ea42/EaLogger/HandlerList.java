package ru.ea42.EaLogger;

import com.sun.net.httpserver.HttpExchange;

import java.util.Map;

public class HandlerList extends HttpHandlerBased {
    @Override
    protected String doGet(HttpExchange exchange, Map params) {
        return "";
    }

    @Override
    protected String doPost(HttpExchange exchange, Map params, String body) {
        return "";
    }
}
