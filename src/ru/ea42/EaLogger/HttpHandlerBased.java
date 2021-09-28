package ru.ea42.EaLogger;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public abstract class HttpHandlerBased implements HttpHandler {
    protected abstract String doGet(HttpExchange exchange, Map params);

    protected abstract String doPost(HttpExchange exchange, Map params,
                                     String body);

    public void handle(HttpExchange exchange) throws IOException {
        // получение параметров url
        String requestMetod = exchange.getRequestMethod();
        String uriStr = exchange.getRequestURI().getQuery();
        String id = UUID.randomUUID().toString().replace("-", "");
        App.LogD("[" + requestMetod + " req " + id + " url] " + uriStr);

        Map pars = new HashMap();
        String[] pairs;
        if (uriStr != null && uriStr.contains("=")) {
            if (uriStr.contains("&"))
                pairs = uriStr.split("&");
            else
                pairs = new String[]{uriStr};
            for (int i = 0; i < pairs.length; i++) {
                String[] p = pairs[i].split("=");
                if (p.length == 2) {
                    pars.put(p[0], p[1]);
                }
            }
        }

        // обрабатываем GET
        String resp = "";
        if (requestMetod.equalsIgnoreCase("GET")) {
            resp = doGet(exchange, pars);
        }

        // обрабатываем POST
        if (requestMetod.equalsIgnoreCase("POST")) {
            String body = InputStreamToUTF8(exchange.getRequestBody());
            App.LogD("[" + requestMetod + " req " + id + " body] " + body);
            resp = doPost(exchange, pars, body);
        }

        // нащ случай частный:
        // - остальные виды запросов не обрабатываем
        // - ответ всегда JSON
        // отправляем ответ
        String resp1 = resp;
        if (resp1 == "")
            resp1 = "<>";
        App.LogD("[" + requestMetod + " rsp " + id + "] " + resp1);

        Headers respHad = exchange.getResponseHeaders();
        // respHad.add("Connection", "Close");
        String[] listS = {"text/html; charset=utf-8"};
        respHad.put("Content-Type", Arrays.asList(listS));
        byte[] bufb = resp.getBytes("UTF8");
        exchange.sendResponseHeaders(200, bufb.length);
        OutputStream body = exchange.getResponseBody();
        body.write(bufb);
        body.close();

        // окончание работы программы
        if (App.Stoped) {
            App.Stop("Server normal stoped");
        }
    }

    // преобразовать файл в UTF-8
    private String InputStreamToUTF8(InputStream is) {
        int ch;
        String Str = "";

        ByteArrayOutputStream sb = new ByteArrayOutputStream();
        try {
            while ((ch = is.read()) != -1)
                sb.write(ch);
            Str = sb.toString("UTF8");
            is.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Str;
    }
}