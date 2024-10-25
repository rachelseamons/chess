package dataaccess;

import model.AuthData;
import model.UserData;

public interface DataAccess {
    UserData getUserByUsername(String username);

    UserData createUser(UserData user);

    AuthData createAuth(String username);

    void clear();

    boolean verifyUser(UserData user);

    AuthData getUserByAuthtoken(String authToken);

    void logoutUser(String authToken);
}
