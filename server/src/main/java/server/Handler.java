package server;

import com.google.gson.Gson;
import dataaccess.DataAccessException;
import model.UserData;
import spark.Request;
import spark.Response;

public class Handler {

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

        return new Gson().toJson(user);
    }
}
