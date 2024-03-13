package dataAccess;

public interface DataAccess {
    void clear() throws DataAccessException;

    boolean userExists(String username);

    void createUser(String username, String password);

    Integer login(String username);
}
