package client;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.JoinRequest;
import server.Server;
import server.ServerFacade;

import java.nio.file.ReadOnlyFileSystemException;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


public class ServerFacadeTests {

    private static Server server;
    private static ServerFacade serverFacade;

    @BeforeAll
    public static void init() {
        server = new Server();
        var port = server.run(0);
        System.out.println("Started test HTTP server on " + port);
        serverFacade = new ServerFacade("http://localhost:" + port);
    }

    @AfterAll
    static void stopServer() {
        server.stop();
    }

    @BeforeEach
    void clearing() throws ResponseException {
        serverFacade.clear();
    }


    @Test
    @DisplayName("register user success")
    public void registerUserSuccess() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);

        Assertions.assertEquals(newUser.username(), registeredUser.username());
        Assertions.assertNotNull(registeredUser.authToken());
    }

    @Test
    @DisplayName("register user bad request")
    public void registerUserBad() throws ResponseException {
        var badUser = new UserData(null, null, null);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.registerUser(badUser));

        String expectedMessage = "failure: 400";
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("register user already taken")
    public void registerUserAlreadyTaken() throws ResponseException {
        var newUser = createTestUser();
        serverFacade.registerUser(newUser);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.registerUser(newUser));

        String expectedMessage = "failure: 403";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("clear")
    public void clear() throws ResponseException {
        //can add more functionality testing everything is emptied
        var newUser = createTestUser();
        Assertions.assertNotNull(serverFacade.registerUser(newUser));
        Assertions.assertThrows(ResponseException.class, () -> serverFacade.registerUser(newUser));

        serverFacade.clear();

        var registeredUser = serverFacade.registerUser(newUser);
        Assertions.assertEquals(newUser.username(), registeredUser.username());
    }

    @Test
    @DisplayName("login success")
    public void loginSuccess() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);

        serverFacade.logoutUser(registeredUser.authToken());

        var loggedInUser = serverFacade.loginUser(newUser);
        Assertions.assertEquals(newUser.username(), loggedInUser.username());
        Assertions.assertNotNull(loggedInUser.authToken());
    }

    @Test
    @DisplayName("fail login with wrong password")
    public void loginWrongPassword() throws ResponseException {
        var newUser = createTestUser();
        var badUser = new UserData(newUser.username(), "bad pass", null);
        var registeredUser = serverFacade.registerUser(newUser);

        serverFacade.logoutUser(registeredUser.authToken());

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.loginUser(badUser));

        String expectedMessage = "failure: 401";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("fail login non-existing user")
    public void loginNotExist() {
        var newUser = createTestUser();
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.loginUser(newUser));

        String expectedMessage = "failure: 401";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("logout success")
    public void logoutSuccess() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);

        serverFacade.logoutUser(registeredUser.authToken());

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.registerUser(newUser));

        String expectedMessage = "failure: 403";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("fail to logout twice")
    public void logoutTwiceFail() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);
        serverFacade.logoutUser(registeredUser.authToken());

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.logoutUser(registeredUser.authToken()));

        String expectedMessage = "failure: 401";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("create game success")
    public void createGameSuccess() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);
        var newGame = createTestGame();
        var createdGame = serverFacade.createGame(registeredUser.authToken(), newGame);

        Assertions.assertEquals(newGame.gameName(), createdGame.gameName());
        Assertions.assertNotEquals(0, createdGame.gameID());
        Assertions.assertNull(createdGame.blackUsername());
        Assertions.assertNull(createdGame.whiteUsername());
    }

    @Test
    @DisplayName("create game no name")
    public void createGameNoName() throws ResponseException {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);
        var badGame = new GameData(0, null, null, null, null);

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.createGame(registeredUser.authToken(), badGame));

        String expectedMessage = "failure: 400";
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("create game bad auth")
    public void createGameBadAuth() {
        var badAuth = "bad";
        var newGame = createTestGame();

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.createGame(badAuth, newGame));

        String expectedMessage = "failure: 401";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("list multiple games")
    public void listMultipleGames() throws ResponseException {
        serverFacade.clear();
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);
        var newGame1 = createTestGame();
        var newGame2 = createTestGame();
        serverFacade.createGame(registeredUser.authToken(), newGame1);
        serverFacade.createGame(registeredUser.authToken(), newGame2);

        var returnedGames = serverFacade.listGames(registeredUser.authToken());
        Set<String> returnedNames = new HashSet<>();
        for (GameData game : returnedGames) {
            returnedNames.add(game.gameName());
        }

        Set<String> expectedNames = new HashSet<>();
        expectedNames.add(newGame1.gameName());
        expectedNames.add(newGame2.gameName());

        Assertions.assertEquals(expectedNames, returnedNames);
    }

    @Test
    @DisplayName("list no games")
    public void listNoGames() throws ResponseException {
        serverFacade.clear();
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);
        var emptySet = new HashSet<GameData>();

        Assertions.assertEquals(emptySet, serverFacade.listGames(registeredUser.authToken()));
    }

    @Test
    @DisplayName("list games bad auth")
    public void listGamesBadAuth() {
        var badAuth = "bad";

        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.listGames(badAuth));

        String expectedMessage = "failure: 401";
        String actualMessage = exception.getMessage();
        int expectedStatus = 401;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("join game success")
    public void joinGameSuccess() throws Exception {
        var newUser = createTestUser();
        var newGame = createTestGame();
        var registeredUser = serverFacade.registerUser(newUser);
        var createdGame = serverFacade.createGame(registeredUser.authToken(), newGame);

        var request = new JoinRequest("WHITE", createdGame.gameID());
        serverFacade.joinGame(registeredUser.authToken(), request);

        var returnedGames = serverFacade.listGames(registeredUser.authToken());
        for (GameData game : returnedGames) {
            if (game.gameID() == createdGame.gameID()) {
                Assertions.assertEquals(registeredUser.username(), game.whiteUsername());
            }
        }
    }

    @Test
    @DisplayName("fail to join when player color is already taken")
    public void failJoinAlreadyTaken() throws Exception {
        var newUser = createTestUser();
        var newGame = createTestGame();
        var registeredUser = serverFacade.registerUser(newUser);
        var createdGame = serverFacade.createGame(registeredUser.authToken(), newGame);

        var request = new JoinRequest("WHITE", createdGame.gameID());
        serverFacade.joinGame(registeredUser.authToken(), request);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.joinGame(registeredUser.authToken(), request));

        String expectedMessage = "failure: 403";
        String actualMessage = exception.getMessage();
        int expectedStatus = 403;
        int actualStatus = exception.getStatusCode();

        Assertions.assertEquals(expectedMessage, actualMessage);
        Assertions.assertEquals(expectedStatus, actualStatus);
    }

    @Test
    @DisplayName("fail to join with wrong gameID")
    public void joinGameWrongID() throws Exception {
        var newUser = createTestUser();
        var registeredUser = serverFacade.registerUser(newUser);

        var request = new JoinRequest("WHITE", 236);
        ResponseException exception = Assertions.assertThrows(ResponseException.class,
                () -> serverFacade.joinGame(registeredUser.authToken(), request));

        String expectedMessage = "failure: 400";
        String actualMessage = exception.getMessage();
        int expectedStatus = 400;
        int actualStatus = exception.getStatusCode();

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
