package dataaccess;

import chess.ChessGame;
import com.google.gson.Gson;
import model.AuthData;
import model.GameData;
import model.UserData;
import org.mindrot.jbcrypt.BCrypt;
import model.JoinRequest;
import service.ChessException;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

import static java.sql.Statement.RETURN_GENERATED_KEYS;

public class SQLDataAccess implements DataAccess {

    public SQLDataAccess() throws DataAccessException {
        configureDatabase();
    }

    @Override
    public UserData getUserByUsername(String username) throws ChessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT username FROM users WHERE username=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var retrievedUser = rs.getString("username");
                        return new UserData(retrievedUser, null, null);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException(ex.getMessage(), 45);
        }
        return null;
    }

    @Override
    public UserData createUser(UserData user) throws ChessException {
        try {
            var username = user.username();
            var email = user.email();
            var password = user.password();
            var statement = "INSERT INTO users (username, email, password) VALUES(?, ?, ?)";
            try (var conn = DatabaseManager.getConnection()) {
                try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                    ps.setString(1, username);
                    ps.setString(2, email);
                    ps.setString(3, password);
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            if (ex.getMessage().startsWith("Duplicate entry")) {
                throw new ChessException("already taken", 403);
            } else {
                throw new ChessException("input too long", 500);
            }
        }
        return new UserData(user.username(), null, user.email());
    }

    @Override
    public AuthData createAuth(String username) throws ChessException {
        if (username == null) {
            throw new ChessException("bad request", 400);
        }
        var authToken = UUID.randomUUID().toString();
        var statement = "INSERT INTO auth (authToken, username) VALUES(?, ?)";
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.setString(2, username);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new ChessException("input too long", 500);
        }
        return new AuthData(authToken, username);
    }

    @Override
    public void clear() throws ChessException {
        List<String> statements = new ArrayList<>();
        statements.add("TRUNCATE users");
        statements.add("TRUNCATE auth");
        statements.add("TRUNCATE games");
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : statements) {
                try (var ps = conn.prepareStatement(statement)) {
                    ps.executeUpdate();
                }
            }
        } catch (Exception ex) {
            throw new ChessException("Error: " + ex.getMessage(), 500);
        }
    }

    @Override
    public boolean verifyUser(UserData user) throws ChessException {
        var statement = "SELECT password FROM users WHERE username=?";
        var username = user.username();
        String hashedPassword = null;
        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        hashedPassword = rs.getString("password");
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException("unauthorized", 401);
        }

        if (hashedPassword == null) {
            throw new ChessException("unauthorized", 401);
        }

        return BCrypt.checkpw(user.password(), hashedPassword);
    }

    @Override
    public AuthData getUserByAuthtoken(String authToken) throws ChessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT authToken, username FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        var retrievedAuth = rs.getString("authToken");
                        var retrievedUsername = rs.getString("username");
                        return new AuthData(retrievedAuth, retrievedUsername);
                    } else {
                        throw new ChessException("unauthorized", 401);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException("unauthorized", 401);
        }
    }

    @Override
    public void logoutUser(String authToken) throws ChessException {
        if (getUserByAuthtoken(authToken) == null) {
            throw new ChessException("unauthorized", 401);
        }

        try (var conn = DatabaseManager.getConnection()) {
            var statement = "DELETE FROM auth WHERE authToken=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, authToken);
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new ChessException("unauthorized", 401);
        }
    }

    @Override
    public GameData createGame(GameData game) throws ChessException {
        var gameName = game.gameName();
        var newGame = new ChessGame();
        var json = new Gson().toJson(newGame);
        int gameID = 0;
        var statement = "INSERT INTO games (gameName, game) VALUES(?, ?)";

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement, RETURN_GENERATED_KEYS)) {
                ps.setString(1, gameName);
                ps.setString(2, json);

                ps.executeUpdate();
                var rs = ps.getGeneratedKeys();
                if (rs.next()) {
                    gameID = rs.getInt(1);
                }
            }
        } catch (Exception ex) {
            throw new ChessException(ex.getMessage(), 500);
        }

        return new GameData(gameID, null, null, gameName, newGame);
    }

    @Override
    public Set<GameData> listGames() throws ChessException {
        Set<GameData> games = new HashSet<>();
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games";
            try (var ps = conn.prepareStatement(statement)) {
                try (var rs = ps.executeQuery()) {
                    while (rs.next()) {
                        games.add(readGame(rs));
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException(ex.getMessage(), 500);
        }

        return games;
    }

    private GameData readGame(ResultSet rs) throws SQLException {
        var gameID = rs.getInt("id");
        var white = rs.getString("whiteUsername");
        var black = rs.getString("blackUsername");
        var gameName = rs.getString("gameName");
        var json = rs.getString("game");
        var game = new Gson().fromJson(json, ChessGame.class);
        return new GameData(gameID, white, black, gameName, game);
    }

    @Override
    public void joinGame(JoinRequest request, String username) throws ChessException {
        var gameID = request.gameID();
        var playerColor = request.playerColor();
        var game = getGameByID(gameID);

        //bad request if player isn't black or white or if game doesn't exist
        if ((!playerColor.equals("BLACK") && !playerColor.equals("WHITE"))
                || game == null) {
            throw new ChessException("bad request", 400);
        }

        //exception if player color is already taken
        if ((playerColor.equals("WHITE") && game.whiteUsername() != null)
                || (playerColor.equals("BLACK") && game.blackUsername() != null)) {
            throw new ChessException("already taken", 403);
        }

        //if the game exists and white is available, add the user as whiteUsername
        addPlayer(request, username);
    }

    private void addPlayer(JoinRequest request, String username) throws ChessException {
        var statement = "";
        if (request.playerColor().equals("WHITE")) {
            statement = "UPDATE games SET whiteUsername=? WHERE id=?";
        } else {
            statement = "UPDATE games SET blackUsername=? WHERE id=?";
        }

        try (var conn = DatabaseManager.getConnection()) {
            try (var ps = conn.prepareStatement(statement)) {
                ps.setString(1, username);
                ps.setInt(2, request.gameID());
                ps.executeUpdate();
            }
        } catch (Exception ex) {
            throw new ChessException(ex.getMessage(), 500);
        }
    }

    private GameData getGameByID(int gameID) throws ChessException {
        try (var conn = DatabaseManager.getConnection()) {
            var statement = "SELECT * FROM games WHERE id=?";
            try (var ps = conn.prepareStatement(statement)) {
                ps.setInt(1, gameID);
                try (var rs = ps.executeQuery()) {
                    if (rs.next()) {
                        return readGame(rs);
                    }
                }
            }
        } catch (Exception ex) {
            throw new ChessException("bad request", 400);
        }
        return null;
    }

    private final String[] createStatements = {
            """
            CREATE TABLE IF NOT EXISTS users (
              username varchar(256) NOT NULL,
              password varchar(256) NOT NULL,
              email varchar(256) NOT NULL,
              PRIMARY KEY (username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS auth (
              authToken varchar(256) NOT NULL,
              username varchar(256) NOT NULL,
              PRIMARY KEY (authToken),
              INDEX(username)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """,

            """
            CREATE TABLE IF NOT EXISTS games (
              id int NOT NULL AUTO_INCREMENT,
              gameName varchar(256) NOT NULL,
              whiteUsername varchar(256),
              blackUsername varchar(256),
              game TEXT NOT NULL,
              PRIMARY KEY (id),
              INDEX(gameName)
            ) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci
            """
    };

    private void configureDatabase() throws DataAccessException {
        DatabaseManager.createDatabase();
        try (var conn = DatabaseManager.getConnection()) {
            for (var statement : createStatements) {
                try (var preparedStatement = conn.prepareStatement(statement)) {
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException ex) {
            throw new DataAccessException("Unable to create configure database");
        }
    }
}
