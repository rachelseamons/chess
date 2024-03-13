package dataAccess;

import model.User;

public interface DataAccess {
    void clear() throws DataAccessException;

    boolean userExists(User user);

    void createUser(User user);

    Integer login(String username) throws DataAccessException;

    boolean verifyUser(String username, String password);
}
