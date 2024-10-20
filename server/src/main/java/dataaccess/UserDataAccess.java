package dataaccess;

import model.UserData;

public interface UserDataAccess {
    String getUserByUsername(String username);

    void createUser(UserData user);
}
