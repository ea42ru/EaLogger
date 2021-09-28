package ru.ea42.EaLogger;

import com.google.gson.JsonObject;
import com.sun.net.httpserver.HttpExchange;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;

public class HandlerList extends HttpHandlerBased {
    protected String doPost(HttpExchange exchange, Map params, String body) {
        return "";
    }

    protected String doGet(HttpExchange exchange, Map params) {
        String strRet = "<html>\r\n<head>\r\n  <meta charset=\"utf-8\">\r\n<title>EaLogger система</title>\r\n"
                + "<style>table {border-collapse: collapse;}\r\n "
                + "td, th {padding: 5px; border: 1px solid #98bf21;}</style>\r\n "
                + "</head>\r\n <body>\r\n<p>" + App.AppVersion + "</p>\r\n <p>Апи: <a href=\"/api01\">localhost:7878/api01</a></p>\r\n";
        // разделитель
        strRet = strRet + "<hr>\r\n";

        // последние 40 собщений лога
        strRet = strRet + "<table>\r\n <caption>Лог (последние 40 строк)</caption>\r\n <tr>\r\n <th>times</th> <th>level</th> <th>sys</th> <th>computer</th> <th>user</th> <th>id</th> <th>mess</th> </tr>\r\n";
        Qu qu = App.QuMan.getQu("SELECT * FROM log ORDER BY times DESC LIMIT 40");
        while (qu.getNext()) {
            strRet = strRet + "<tr>\r\n <td>" + qu.asString("times") + "</td><td>" + qu.asString("level") + "</td><td>"
                    + qu.asString("sys") + "</td><td>" + qu.asString("computer") + "</td><td>" + qu.asString("user")
                    + "</td><td>" + qu.asString("id") + "</td><td>" + qu.asString("mess") + "<td>\r\n </tr>\r\n";
        }
        qu.release();
        strRet = strRet + "</table><hr>\r\n";

        // текст АПИ
        strRet = strRet + getHelp() + "<hr>\r\n";
        strRet = strRet + "</body>\r\n</html>";
        return strRet;
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