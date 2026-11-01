package dataaccess.sql;

import dataaccess.DataAccessException;
import model.UserData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLUserDAOTests {
    private SQLUserDAO userDAO;

    @BeforeEach
    public void setUp() throws DataAccessException {
        userDAO = new SQLUserDAO();
        userDAO.clear();
    }

    @Test
    public void createAndGetUser() throws DataAccessException {
        UserData user = new UserData("hamid", "testHash", "hamid@example.com");
        userDAO.createUser(user);
        UserData storedUser = userDAO.getUser("hamid");

        assertEquals(user, storedUser);
    }

    @Test
    public void getMissingUser() throws DataAccessException {
        assertNull(userDAO.getUser("missing"));
    }

    @Test
    public void rejectDuplicateUsername() throws DataAccessException {
        UserData original = new UserData("hamid", "testHash", "hamid@example.com");
        userDAO.createUser(original);
        UserData duplicate = new UserData("hamid", "otherHash", "other@example.com");

        assertThrows(DataAccessException.class, () -> userDAO.createUser(duplicate));
        assertEquals(original, userDAO.getUser("hamid"));
    }

    @Test
    public void clearRemovesAllUsers() throws DataAccessException {
        userDAO.createUser(new UserData("hamid", "hashOne", "hamid@example.com"));
        userDAO.createUser(new UserData("alia", "hashTwo", "alia@example.com"));
        userDAO.clear();

        assertNull(userDAO.getUser("hamid"));
        assertNull(userDAO.getUser("alia"));
    }

    @Test
    public void userPersistsAcrossDAOInstances() throws DataAccessException {
        UserData user = new UserData("hamid", "testHash", "hamid@example.com");
        userDAO.createUser(user);
        SQLUserDAO anotherDAO = new SQLUserDAO();

        assertEquals(user, anotherDAO.getUser("hamid"));
    }
}