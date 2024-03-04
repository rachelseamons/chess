package server;

import dataAccess.DataAccessMemory;
import service.ChessService;
import spark.*;

public class Server {
    ChessService service;

    public Server(ChessService service) {
        this.service = service;
    }

    public Server() {
        var dataAccess = new DataAccessMemory();
        service = new ChessService(dataAccess);
    }

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.

        Spark.awaitInitialization();
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
