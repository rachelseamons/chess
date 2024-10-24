package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.GameData;
import model.UserData;
import service.ChessException;
import service.Service;
import spark.*;

import java.util.HashMap;

public class Server {
    private final Gson serializer = new Gson();
    private final Service service = new Service(new MemoryDataAccess());

    public int run(int desiredPort) {
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
    }


    private Object joinGame(Request request, Response response) {
        var authToken = request.headers("Authorization");
        //TODO:: I have no clue how to get the info from the request, short of creating another record
        return new Gson().toJson("join game");
    }

    private Object createGame(Request request, Response response) {
        var authToken = request.headers("Authorization");
        var game = new Gson().fromJson(request.body(), GameData.class);

        return new Gson().toJson("create game");
    }

    private Object getGames(Request request, Response response) {
        var authToken = request.headers("Authorization");
        return new Gson().toJson("get games");
    }

    private Object logoutUser(Request request, Response response) {
        var authToken = request.headers("Authorization");
        return new Gson().toJson("logout");
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
        //TODO:: figure out why the tests don't like this response form despite pulling the string out correctly
        if (ex instanceof ChessException) {
            res.status(((ChessException) ex).getStatus());
            String returnMessage = "Error: " + ex.getMessage();
            res.body(serializer.toJson(returnMessage));
        } else {
            res.status(500);
            res.body(serializer.toJson("Error: (description of error)"));
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
