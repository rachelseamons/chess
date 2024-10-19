package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.GameData;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

import java.util.Map;
import java.util.Set;

public class Handler {
    private UserService userService = new UserService();
    private AuthService authService = new AuthService();
    private GameService gameService = new GameService();

    public Object clear(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("clear");
    }

    public Object joinGame(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("Authorization");
        //TODO:: I have no clue how to get the info from the request, short of creating another record
        return new Gson().toJson("join game");
    }

    public Object createGame(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("Authorization");
        var game = new Gson().fromJson(request.body(), GameData.class);

        return new Gson().toJson("create game");
    }

    public Object getGames(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("Authorization");
        return new Gson().toJson("get games");
    }

    public Object logoutUser(Request request, Response response) throws DataAccessException {
        var authToken = request.headers("Authorization");
        return new Gson().toJson("logout");
    }

    public Object loginUser(Request request, Response response) throws DataAccessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null) {
            System.out.println("in fail 1");
            response.status(400);
            var message = "Error: bad request";
            return new Gson().toJson(message);
        }

        return new Gson().toJson(user);
    }

    public Object registerUser(Request request, Response response) throws DataAccessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null || user.email() == null) {
            System.out.println("in fail 1");
            response.status(400);
            var message = "Error: bad request";
            return new Gson().toJson(message);
        }

        //TODO:: add try-catch block to handle syntax errors in requests
        // actually specs say to do that in the server class, so GAH
        var confirmation = userService.registerUser(user);
        return new Gson().toJson(confirmation);
    }
}
