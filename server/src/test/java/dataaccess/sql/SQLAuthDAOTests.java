package dataaccess.sql;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class SQLAuthDAOTests {
    private SQLAuthDAO authDAO;

    @BeforeEach
    public void setUp() throws DataAccessException{
        authDAO = new SQLAuthDAO();
        authDAO.clear();
    }

    @Test
    public void createAndGetAuth() throws DataAccessException{
        AuthData auth = new AuthData("007", "James Bond");
        authDAO.createAuth(auth);
        AuthData storedAuth = authDAO.getAuth("007");

        assertEquals(auth, storedAuth);
    }

    @Test
    public void missingToken() throws DataAccessException{
        assertNull(authDAO.getAuth("spooky ghost token, very scary.."));
    }

    @Test
    public void rejectDuplicateToken() throws DataAccessException{
        AuthData auth = new AuthData("007", "James Bond");
        AuthData secondAuth = new AuthData("007", "Austin Powers");
        authDAO.createAuth(auth);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(secondAuth));
        assertEquals(auth, authDAO.getAuth("007"));
    }

}
