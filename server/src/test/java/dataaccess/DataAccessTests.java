package dataaccess;

import model.AuthData;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import server.JoinRequest;
import service.ChessException;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class DataAccessTests {
    private final DataAccess dataAccess = new SQLDataAccess();

    public DataAccessTests() throws DataAccessException {
    }

    @BeforeEach
    public void setUp() throws Exception {
        dataAccess.clear();
    }

    @Test
    @DisplayName("fail to get user with empty set")
    public void emptySet() throws ChessException {
        dataAccess.clear();
        Assertions.assertNull(dataAccess.getUserByUsername("test"));
    }

    @Test
    @DisplayName("get user by username success")
    public void getExistingUser() throws ChessException {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        dataAccess.createUser(newUser);

        var user = dataAccess.getUserByUsername(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.username());
    }

    @Test
    @DisplayName("add user success")
    public void createUser() throws Exception {
        var username = UUID.randomUUID().toString();
        UserData newUser = new UserData(username, "password", "@me");
        var createdUser = dataAccess.createUser(newUser);
        Assertions.assertNotNull(createdUser);
        Assertions.assertEquals(username, createdUser.username());

        var user = dataAccess.getUserByUsername(username);
        Assertions.assertNotNull(user);
        Assertions.assertEquals(username, user.username());
    }

    @Test
    @DisplayName("fail to add existing user")
    public void createUserFail() throws Exception {
        var newUser = createTestUser();
        dataAccess.createUser(newUser);

        ChessException exception = Assertions.assertThrows(ChessException.class, () -> dataAccess.createUser(newUser));
        String expectedMessage = "already taken";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("create auth success")
    public void createAuth() throws Exception {
        var username = UUID.randomUUID().toString();
        AuthData newAuth = dataAccess.createAuth(username);

        Assertions.assertNotNull(newAuth);
        Assertions.assertEquals(username, newAuth.username());
    }

    @Test
    @DisplayName("fail to create auth with no username")
    public void failCreateAuth() {
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.createAuth(null));
        String expectedMessage = "bad request";
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("verify user successfully")
    public void verifyUserSuccess() throws Exception {
        var newUser = createTestUser();
        var hashedPassword = BCrypt.hashpw(newUser.password(), BCrypt.gensalt());
        var encryptedUser = new UserData(newUser.username(), hashedPassword, newUser.email());
        dataAccess.createUser(encryptedUser);

        Assertions.assertTrue(dataAccess.verifyUser(newUser));
    }

    @Test
    @DisplayName("fail to verify non-existent user")
    public void verifyNonexistentUser() {
        var newUser = createTestUser();
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.verifyUser(newUser));
        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("get user by authtoken success")
    public void getByAuthSuccess() throws Exception {
        var newUser = createTestUser();
        AuthData newAuth = dataAccess.createAuth(newUser.username());
        var authToken = newAuth.authToken();

        var retrievedAuth = dataAccess.getUserByAuthtoken(authToken);

        Assertions.assertEquals(authToken, retrievedAuth.authToken());
        Assertions.assertEquals(newAuth.username(), retrievedAuth.username());
    }

    @Test
    @DisplayName("fail get user by authtoken")
    public void getByAuthFail() {
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.getUserByAuthtoken(null));
        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("logout success")
    public void logoutSuccess() throws Exception {
        var newUser = createTestUser();
        var newAuth = dataAccess.createAuth(newUser.username());

        Assertions.assertDoesNotThrow(() -> dataAccess.logoutUser(newAuth.authToken()));
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.getUserByAuthtoken(newAuth.authToken()));
        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("fail to logout user twice")
    public void logoutFail() throws Exception {
        var newUser = createTestUser();
        var newAuth = dataAccess.createAuth(newUser.username());

        Assertions.assertDoesNotThrow(() -> dataAccess.logoutUser(newAuth.authToken()));
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.logoutUser(newAuth.authToken()));
        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("create game success")
    public void createGameSuccess() throws Exception {
        dataAccess.clear();
        var game = createTestGame();
        var createdGame = dataAccess.createGame(game);

        Assertions.assertEquals(game.gameName(), createdGame.gameName());
        Assertions.assertEquals(1, createdGame.gameID());

        game = createTestGame();
        createdGame = dataAccess.createGame(game);

        Assertions.assertEquals(game.gameName(), createdGame.gameName());
        Assertions.assertEquals(2, createdGame.gameID());
    }

    @Test
    @DisplayName("fail to create game with no gameName")
    public void createGameFail() {
        var game = new GameData(0, null, null, null, null);
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.createGame(game));

        int expectedStatus = 500;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("list games success")
    public void listGamesSuccess() throws Exception {
        Set<GameData> games = new HashSet<>();
        for (int i = 0; i < 10; i++) {
            var game = createTestGame();
            games.add(game);
            dataAccess.createGame(game);
        }

        var retrievedGames = dataAccess.listGames();

        Set<String> retrievedNames = new HashSet<>();
        Set<String> expectedNames = new HashSet<>();
        for (GameData game : retrievedGames) {
            retrievedNames.add(game.gameName());
        }
        for (GameData game : games) {
            expectedNames.add(game.gameName());
        }

        Assertions.assertEquals(expectedNames, retrievedNames);
    }

    @Test
    @DisplayName("list no games")
    public void listNoGames() throws Exception {
        dataAccess.clear();
        var games = dataAccess.listGames();
        Assertions.assertEquals(new HashSet<>(), games);
    }

    @Test
    @DisplayName("join game success")
    public void joinGameSuccess() throws Exception {
        dataAccess.clear();
        var game = createTestGame();
        var userOne = createTestUser();
        var userTwo = createTestUser();

        game = dataAccess.createGame(createTestGame());
        dataAccess.createUser(userOne);
        dataAccess.createUser(userTwo);

        var joinRequest = new JoinRequest("WHITE", game.gameID());
        dataAccess.joinGame(joinRequest, userOne.username());
        joinRequest = new JoinRequest("BLACK", game.gameID());
        dataAccess.joinGame(joinRequest, userTwo.username());

        var retrievedGames = dataAccess.listGames();
        var size = retrievedGames.size();
        Assertions.assertEquals(1, size);

        GameData updatedGame = new GameData(0, null, null, null, null);
        for (GameData retrievedGame : retrievedGames) {
            updatedGame = retrievedGame;
        }

        Assertions.assertEquals(userOne.username(), updatedGame.whiteUsername());
        Assertions.assertEquals(userTwo.username(), updatedGame.blackUsername());
        Assertions.assertEquals(game.gameID(), updatedGame.gameID());
        Assertions.assertEquals(game.gameName(), updatedGame.gameName());
    }

    @Test
    @DisplayName("fail to join where player color already taken")
    public void joinAlreadyTaken() throws Exception {
        dataAccess.clear();
        var game = createTestGame();
        var userOne = createTestUser();
        var userTwo = createTestUser();

        game = dataAccess.createGame(createTestGame());
        dataAccess.createUser(userOne);
        dataAccess.createUser(userTwo);

        var joinRequest = new JoinRequest("WHITE", game.gameID());
        dataAccess.joinGame(joinRequest, userOne.username());
        var failRequest = new JoinRequest("WHITE", game.gameID());
        ChessException exception = Assertions.assertThrows(ChessException.class,
                () -> dataAccess.joinGame(failRequest, userTwo.username()));

        String expectedMessage = "already taken";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }


    private UserData createTestUser() {
        var username = UUID.randomUUID().toString();
        var hashedPassword = UUID.randomUUID().toString();
        return new UserData(username, hashedPassword, "@me");
    }

    private GameData createTestGame() {
        var gameName = UUID.randomUUID().toString();
        return new GameData(0, null, null, gameName, null);
    }
}
