package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import spark.*;

public class Server {

    public int run(int desiredPort) {
        Spark.port(desiredPort);

        Spark.staticFiles.location("web");

        var handler = new Handler();

        // Register your endpoints and handle exceptions here.
        Spark.post("/user", handler::registerUser);
        Spark.post("/session", handler::loginUser);
        Spark.delete("/session", handler::logoutUser);
        Spark.get("/game", handler::getGames);
        Spark.post("/game",handler::createGame);
        Spark.put("/game",handler::joinGame);
        Spark.delete("/db",handler::clear);
        //This line initializes the server and can be removed once you have a functioning endpoint 
        Spark.init();

        Spark.awaitInitialization();
        return Spark.port();
    }



    public int port() {
        return Spark.port();
    }

    public void stop() {
        Spark.stop();
        Spark.awaitStop();
    }
}
