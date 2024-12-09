package service;

import chess.ChessGame;
import dataaccess.SQLDataAccess;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mindrot.jbcrypt.BCrypt;
import model.JoinRequest;

import java.util.HashSet;
import java.util.Set;

public class ServiceTests {
    @BeforeAll
    static void createVariables() throws Exception {
        service = new Service(new SQLDataAccess());
    }

    static private Service service;
    static private final UserData USER_FRED = new UserData("Fred", "password", "@me");
    static private final UserData USER_SUE = new UserData("Sue", "pass", "@you");
    static private final UserData LOGIN_FRED = new UserData("Fred", "password", null);
    static private final UserData WRONG_PASSWORD_SUE = new UserData("Sue", "Pass", null);
    static private final GameData GOOD_GAME_1 = new GameData(0, null, null,
            "myGame", null);
    static private final GameData BAD_GAME = new GameData(0, null, null,
            null, null);
    static private final GameData GOOD_GAME_2 = new GameData(0, null, null,
            "game 2", null);

    @Test
    @DisplayName("Successful register user")
    public void registerUserSuccess() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        Assertions.assertEquals(USER_FRED.username(), userAuth.username());
        Assertions.assertNotNull(userAuth.authToken());
    }


    @Test
    @DisplayName("Fail to register existing user")
    public void registerUserFail() throws ChessException {
        service.clear();
        service.registerUser(USER_FRED);
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.registerUser(USER_FRED));

        String expectedMessage = "already taken";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Clear database")
    public void clearDatabase() throws ChessException {
        //this test could be enhanced with more added to the database using various functions
        service.clear();

        Assertions.assertDoesNotThrow(() -> service.registerUser(USER_FRED));
        Assertions.assertThrows(ChessException.class, () ->
                service.registerUser(USER_FRED));

        var userAuth = service.registerUser(USER_SUE);
        Assertions.assertEquals(USER_SUE.username(), userAuth.username());

        Assertions.assertDoesNotThrow(service::clear);
        Assertions.assertDoesNotThrow(() -> service.registerUser(USER_FRED));
    }

    @Test
    @DisplayName("Successful login")
    public void successfulLogin() throws ChessException {
        service.clear();
        var hashedPassword = BCrypt.hashpw(USER_FRED.password(), BCrypt.gensalt());
        service.registerUser(new UserData(USER_FRED.username(), hashedPassword, USER_FRED.email()));

        var userAuth = service.loginUser(LOGIN_FRED);
        Assertions.assertEquals(USER_FRED.username(), userAuth.username());
        Assertions.assertNotNull(userAuth.authToken());
    }

    @Test
    @DisplayName("Login non-existent user")
    public void loginBadUser() throws ChessException {
        service.clear();
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.loginUser(LOGIN_FRED));

        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Login wrong password")
    public void loginWrongPassword() throws ChessException {
        service.clear();
        var hashedPassword = BCrypt.hashpw(USER_SUE.password(), BCrypt.gensalt());
        service.registerUser(new UserData(USER_SUE.username(), hashedPassword, USER_SUE.email()));

        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.loginUser(WRONG_PASSWORD_SUE));

        String expectedMessage = "unauthorized";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatus();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("Successful logout")
    public void logoutSuccess() throws ChessException {
        service.clear();
        var hashedPassword = BCrypt.hashpw(USER_FRED.password(), BCrypt.gensalt());
        var firstAuth = service.registerUser(new UserData(USER_FRED.username(), hashedPassword, USER_FRED.email()));

        Assertions.assertDoesNotThrow(() -> service.logoutUser(firstAuth.authToken()));
        var secondAuth = Assertions.assertDoesNotThrow(() -> service.loginUser(USER_FRED));
        Assertions.assertDoesNotThrow(() -> service.logoutUser(secondAuth.authToken()));
    }

    @Test
    @DisplayName("Logout twice")
    public void logoutTwice() throws ChessException {
        service.clear();

        var userAuth = service.registerUser(USER_FRED);

        Assertions.assertDoesNotThrow(() -> service.logoutUser(userAuth.authToken()));
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.logoutUser(userAuth.authToken()));

        Assertions.assertEquals(401, exception.getStatus());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("Successful create")
    public void createSuccess() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        var createdGame = service.createGame(userAuth.authToken(), GOOD_GAME_1);

        Assertions.assertEquals(GOOD_GAME_1.gameName(), createdGame.gameName());
    }

    @Test
    @DisplayName("unauthorized create")
    public void unauthorizedCreate() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        service.logoutUser(userAuth.authToken());
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.createGame(userAuth.authToken(), BAD_GAME));

        Assertions.assertEquals(401, exception.getStatus());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("list no games")
    public void listNoGames() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        var games = service.listGames(userAuth.authToken());

        Assertions.assertEquals(new HashSet<>(), games);
    }

    @Test
    @DisplayName("list multiple games")
    public void listMultipleGames() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        service.createGame(userAuth.authToken(), GOOD_GAME_1);
        service.createGame(userAuth.authToken(), GOOD_GAME_2);

        var returnedGames = service.listGames(userAuth.authToken());
        Set<String> returnedNames = new HashSet<>();
        for (GameData game : returnedGames) {
            returnedNames.add(game.gameName());
        }

        Set<String> expectedNames = new HashSet<>();
        expectedNames.add(GOOD_GAME_1.gameName());
        expectedNames.add(GOOD_GAME_2.gameName());

        Assertions.assertEquals(expectedNames, returnedNames);
    }

    @Test
    @DisplayName("unauthorized list")
    public void unauthorizedListGames() throws ChessException {
        service.clear();
        var userAuth = service.registerUser(USER_FRED);
        service.createGame(userAuth.authToken(), GOOD_GAME_1);
        service.createGame(userAuth.authToken(), GOOD_GAME_2);

        service.logoutUser(userAuth.authToken());
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.listGames(userAuth.authToken()));

        Assertions.assertEquals(401, exception.getStatus());
        Assertions.assertEquals("unauthorized", exception.getMessage());
    }

    @Test
    @DisplayName("successful game joins")
    public void joinGameSuccesses() throws ChessException {
        service.clear();
        var fredAuth = service.registerUser(USER_FRED).authToken();
        var sueAuth = service.registerUser(USER_SUE).authToken();
        var gameID = service.createGame(fredAuth, GOOD_GAME_1).gameID();

        JoinRequest joinFredAsBlack = new JoinRequest("BLACK", gameID);
        JoinRequest joinSueAsWhite = new JoinRequest("WHITE", gameID);

        Assertions.assertDoesNotThrow(() -> service.joinGame(fredAuth, joinFredAsBlack));
        Assertions.assertDoesNotThrow(() -> service.joinGame(sueAuth, joinSueAsWhite));

        GameData expectedGame = new GameData(gameID, "Sue", "Fred", "myGame", new ChessGame());

        GameData actualGame = new GameData(0, null, null, null, null);
        for (GameData game : service.listGames(fredAuth)) {
            actualGame = game;
        }

        Assertions.assertEquals(expectedGame.gameID(), actualGame.gameID());
        Assertions.assertEquals(expectedGame.whiteUsername(), actualGame.whiteUsername());
        Assertions.assertEquals(expectedGame.blackUsername(), actualGame.blackUsername());
        Assertions.assertEquals(expectedGame.gameName(), actualGame.gameName());
    }

    @Test
    @DisplayName("color already taken")
    public void badJoin() throws ChessException {
        service.clear();
        var fredAuth = service.registerUser(USER_FRED).authToken();
        var sueAuth = service.registerUser(USER_SUE).authToken();
        var gameID = service.createGame(fredAuth, GOOD_GAME_1).gameID();

        JoinRequest joinFredAsBlack = new JoinRequest("BLACK", gameID);
        JoinRequest joinSueAsBlack = new JoinRequest("BLACK", gameID);

        Assertions.assertDoesNotThrow(() -> service.joinGame(fredAuth, joinFredAsBlack));
        ChessException exception = Assertions.assertThrows(ChessException.class, () ->
                service.joinGame(sueAuth, joinSueAsBlack));

        Assertions.assertEquals(403, exception.getStatus());
        Assertions.assertEquals("already taken", exception.getMessage());
    }
}