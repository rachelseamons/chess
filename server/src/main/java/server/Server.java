package server;

import com.google.gson.Gson;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", this::registerUser);
        Spark.post("/session", this::loginUser);
        Spark.delete("/session", this::logoutUser);
        Spark.get("/game", this::getGames);
        Spark.post("/game",this::createGame);
        Spark.put("/game",this::joinGame);
        Spark.delete("/db",this::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }

    private Object clear(Request request, Response response) {
        return new Gson().toJson("clear");
    }

    private Object joinGame(Request request, Response response) {
        return new Gson().toJson("join game");
    }

    private Object createGame(Request request, Response response) {
        return new Gson().toJson("create game");
    }

    private Object getGames(Request request, Response response) {
        return new Gson().toJson("get games");
    }

    private Object logoutUser(Request request, Response response) {
        return new Gson().toJson("logout");
    }

    private Object loginUser(Request request, Response response) {
        return new Gson().toJson("login");
    }

    private Object registerUser(Request request, Response response) {
        return new Gson().toJson("register");
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
