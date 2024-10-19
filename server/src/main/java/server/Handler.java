package server;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import dataaccess.DataAccessException;
import model.UserData;
import service.AuthService;
import service.GameService;
import service.UserService;
import spark.Request;
import spark.Response;

public class Handler {
    private UserService userService = new UserService();
    private AuthService authService = new AuthService();
    private GameService gameService = new GameService();

    public Object clear(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("clear");
    }

    public Object joinGame(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("join game");
    }

    public Object createGame(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("create game");
    }

    public Object getGames(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("get games");
    }

    public Object logoutUser(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("logout");
    }

    public Object loginUser(Request request, Response response) throws DataAccessException {
        return new Gson().toJson("login");
    }

    public Object registerUser(Request request, Response response) throws DataAccessException {
        var user = new Gson().fromJson(request.body(), UserData.class);
        //TODO:: add try-catch block to handle syntax errors in requests
        var confirmation = userService.registerUser(user);

        if (user.username() == null || user.password() == null || user.email() == null) {
            System.out.println("in fail 1");
            response.status(400);
            var message = "Error: bad request";
            return new Gson().toJson(message);
        } else {
            return new Gson().toJson(confirmation);
        }
    }
}
