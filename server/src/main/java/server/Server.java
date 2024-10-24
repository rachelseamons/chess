package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.UserData;
import service.ChessException;
import service.Service;
import spark.*;

import java.util.HashMap;

public class Server {
    private Handler handler = new Handler();
    private final Gson serializer = new Gson();
    private Service service = new Service(new MemoryDataAccess());

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
        return null;
    }

    private Object createGame(Request request, Response response) {
        return null;
    }

    private Object getGames(Request request, Response response) {
        return null;
    }

    private Object logoutUser(Request request, Response response) {
        return null;
    }

    private Object loginUser(Request request, Response response) {
        return null;
    }

    private Object clear(Request request, Response response) {
        service.clear();
        return serializer.toJson(new HashMap<>());
    }

    private Object registerUser(Request request, Response response) throws DataAccessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("400");
        }

        var userAuth = service.registerUser(user);

        return serializer.toJson(userAuth);
    }

    private void exceptionHandler(Exception ex, Request req, Response res) {
        if (ex instanceof ChessException) {
            if (ex.getMessage().equals("Error")) {
                //TODO:: add if blocks for all your messages
                res.status(400);
            }
        }
        res.body("Error: " + ex.getMessage());
    }

    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
