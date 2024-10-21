package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class AuthMemoryDAO implements AuthDAO {
    private Set<AuthData> authData = new HashSet<>();

    public AuthData createAuth(String username) {
        var authToken = generateToken();
        AuthData newData = new AuthData(authToken, username);
        authData.add(newData);
        return newData;
    }

    public static String generateToken() {
        return UUID.randomUUID().toString();
    }
}
