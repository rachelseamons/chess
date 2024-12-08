package client;

import exception.ResponseException;
import model.GameData;
import model.UserData;
import org.junit.jupiter.api.*;
import server.Server;
import server.ServerFacade;

import java.nio.file.ReadOnlyFileSystemException;
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
