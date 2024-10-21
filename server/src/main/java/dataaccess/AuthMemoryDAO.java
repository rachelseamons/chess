package dataaccess;

import model.AuthData;
import model.UserData;

import java.util.HashSet;
import java.util.Set;

public class AuthMemoryDAO implements AuthDAO {
    private Set<UserData> authData = new HashSet<>();

    public AuthData createAuth(String username) throws DataAccessException {
        return null;
        //TODO:: start here
    }
}
