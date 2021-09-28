package ru.ea42.EaLogger;

import com.sun.net.httpserver.HttpServer;
import ru.ea42.lib.config.Config;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.text.SimpleDateFormat;
import java.util.Date;

public class App {
    // глобальные переменные
    public static String AppVersion = "(c) EaLogger Евтюшкин А.В. v 1.01 [14.10.2020]";
    public static int Httpport;
    public static String DBHost;
    public static String DBPort;
    public static String DBUser;
    public static String DBPassword;
    public static String DBDB;
    public static boolean Debug;
    public static boolean Stoped;
    public static HttpServer Http = null;
    public static QuManager QuMan;

    // App - синглетон
    private static App instance;

    public static synchronized App getInstace() {
        if (instance == null)
            instance = new App();
        return instance;
    }

    // старт сервиса
    public static synchronized void Start() {
        Debug = true;
        Stoped = false;

        // считываем настройки
        Config co = new Config();
        if (co.jsEmpty()) App.Stop("not config");
        Debug = co.getParamAsBoolean("Debug");
        Httpport = co.getParamAsInt("Port");
        DBHost = co.getParamAsString("DB", "Host");
        DBPort = co.getParamAsString("DB", "Port");
        DBUser = co.getParamAsString("DB", "User");
        DBPassword = co.getParamAsString("DB", "Password");
        DBDB = co.getParamAsString("DB", "DB");

        // подключение к базе данных
        QuMan = new QuManager();
        if (App.QuMan.testFail()) App.Stop("db not found");

        // запуск http сервера
        // HttpServer server;
        try {
            Http = HttpServer.create(new InetSocketAddress(App.Httpport), 0);
            Http.createContext("/api01", new HandlerApi01());
            Http.createContext("/", new HandlerList());
            //server.setExecutor(Executors.newCachedThreadPool());
            Http.setExecutor(null);
            Http.start();
            App.Log("Server started on port: " + App.Httpport);
        } catch (IOException e) {
            e.printStackTrace();
            App.Stop("http server not started");
        }
    }

    // остановка сервиса
    public static synchronized void Stop(String msg) {
        if (Http != null)
            Http.stop(0);
        App.Log(msg);
        System.exit(0);
    }

    // логирование
    public static synchronized void Log(String msg) {
        System.out.println(new SimpleDateFormat("dd.MM.yy HH:mm:ss:SSS").format(new Date()) + " - " + msg);
    }

    // отладочное логирование
    public static synchronized void LogD(String msg) {
        if (App.Debug)
            Log(msg);
    }
}