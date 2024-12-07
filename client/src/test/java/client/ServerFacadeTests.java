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
        ResponseException exception = Assertions.assertThrows(ResponseException.class, () -> serverFacade.registerUser(badUser));

        String expectedMessage = "Bad Request";
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
