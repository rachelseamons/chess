package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import spark.*;

public class Server {
    private Handler handler = new Handler();

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

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        return null;
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

    private Object registerUser(Request request, Response response) throws DataAccessException {
        //TODO:: attempt at an example of an error-handling block,
        // I don't think this is fully right. Add other messages to if-else in the catch though
        try {
            return handler.registerUser(request);
        } catch (DataAccessException fail) {
            if (fail.getMessage() == "403") {
                response.status(403);
                return new Gson().toJson("Error: already taken");
            } else {
                response.status(500);
                return new Gson().toJson("Error: (description of error)");
            }
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
