package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import dataaccess.MemoryDataAccess;
import model.AuthData;
import model.GameData;
import model.UserData;
import service.Service;
import spark.Request;
import spark.Response;

public class Handler {
    private Service userService;

    public Handler() {
        userService = new Service(new MemoryDataAccess());
    }

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

    public AuthData registerUser(Request request) throws DataAccessException {
        var user = new Gson().fromJson(request.body(), UserData.class);

        if (user.username() == null || user.password() == null || user.email() == null) {
            throw new DataAccessException("400");
        }

        return userService.registerUser(user);
    }
}
