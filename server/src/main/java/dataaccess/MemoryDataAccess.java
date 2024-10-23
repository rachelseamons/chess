package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class MemoryDataAccess implements DataAccess {
    private final Map<String, UserData> users = new HashMap<>();
    private final Map<String, AuthData> auths = new HashMap<>();

    public UserData getUserByUsername(String username) {
        if (users.containsKey(username)) {
            return users.get(username);
        }
        return null;
    }

    public UserData createUser(UserData user) {
        users.put(user.username(), user);
        return user;
    }

    public AuthData createAuth(String username) {
        var authToken = generateToken();
        AuthData newData = new AuthData(authToken, username);
        auths.put(authToken, newData);
        return newData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}