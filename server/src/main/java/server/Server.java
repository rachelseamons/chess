package server;

import com.google.gson.Gson;
import dataaccess.DataAccess;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import dataaccess.SQLDataAccess;
import model.GameData;
import model.UserData;
import service.ChessException;
import service.Service;
import spark.*;

import java.util.HashMap;
import java.util.Map;

public class Server {
    private final Gson serializer = new Gson();
    //private final Service service = new Service(new MemoryDataAccess());
    private Service service;


    public int run(int desiredPort) {
        try {
            service = new Service(new SQLDataAccess());
            Spark.port(desiredPort);

            Spark.staticFiles.location("web");

            // Register your endpoints and handle exceptions here.
            Spark.post("/user", this::registerUser);
            Spark.post("/session", this::loginUser);
            Spark.delete("/session", this::logoutUser);
            Spark.get("/game", this::getGames);
            Spark.post("/game", this::createGame);
            Spark.put("/game", this::joinGame);
            Spark.delete("/db", this::clear);
            //This line initializes the server and can be removed once you have a functioning endpoint
            Spark.init();

            Spark.exception(Exception.class, this::exceptionHandler);

            Spark.awaitInitialization();
            return Spark.port();
        } catch (DataAccessException ex) {
            System.out.println("Error: " + ex);
            return -1;
        }
    }


    private Object joinGame(Request request, Response response) throws ChessException {
        var authToken = request.headers("Authorization");
        var joinRequest = new Gson().fromJson(request.body(), JoinRequest.class);

        if (joinRequest.gameID() == null || joinRequest.playerColor() == null) {
            throw new ChessException("bad request", 400);
        }

        service.joinGame(authToken, joinRequest);
        return new Gson().toJson(null);
    }

    private Object createGame(Request request, Response response) throws ChessException {
        var authToken = request.headers("Authorization");
        var game = new Gson().fromJson(request.body(), GameData.class);

        if (game.gameName() == null) {
            throw new ChessException("bad request", 400);
        }

        var createdGame = service.createGame(authToken, game);

        return new Gson().toJson(createdGame);
    }

    private Object getGames(Request request, Response response) throws ChessException {
        var authToken = request.headers("Authorization");

        var gameList = service.listGames(authToken);
        return new Gson().toJson(Map.of("games", gameList));
    }

    private Object logoutUser(Request request, Response response) throws ChessException {
        var authToken = request.headers("Authorization");

        service.logoutUser(authToken);
        return serializer.toJson(null);
    }

    private Object loginUser(Request request, Response response) throws ChessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null) {
            throw new ChessException("bad request", 400);
        }

        var userAuth = service.loginUser(user);
        return new Gson().toJson(userAuth);
    }

    private Object clear(Request request, Response response) {
        service.clear();
        return serializer.toJson(new HashMap<>());
    }

    private Object registerUser(Request request, Response response) throws ChessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new ChessException("bad request", 400);
        }

        var userAuth = service.registerUser(user);

        return serializer.toJson(userAuth);
    }

    private void exceptionHandler(Exception ex, Request req, Response res) {
        if (ex instanceof ChessException) {
            res.status(((ChessException) ex).getStatus());
            String returnMessage = "Error: " + ex.getMessage();
            Map<String, String> body = Map.of("message", returnMessage);
            res.body(serializer.toJson(body));
        } else {
            res.status(500);
            res.body(serializer.toJson(Map.of("message", "Error: (description of error)")));
        }
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
