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

    @Test
    public void rejectNullUsername() throws DataAccessException{
        AuthData auth = new AuthData("wrong-token", null);
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
        assertNull(authDAO.getAuth("wrong-token"));
    }

    @Test
    public void rejectNullToken() throws DataAccessException{
        AuthData auth = new AuthData(null, "Justin Bieber");
        assertThrows(DataAccessException.class, () -> authDAO.createAuth(auth));
    }

    @Test
    public void multipleTokensSameUser() throws DataAccessException{
        AuthData auth = new AuthData("On Her Majesty's", "James Bond");
        AuthData secondAuth = new AuthData("Secret Service", "James Bond");
        authDAO.createAuth(auth);
        authDAO.createAuth(secondAuth);

        assertEquals(auth, authDAO.getAuth("On Her Majesty's"));
        assertEquals(secondAuth, authDAO.getAuth("Secret Service"));
    }

    @Test
    public void multipleTokensDeleteOne() throws DataAccessException{
        AuthData auth = new AuthData("On Her Majesty's", "James Bond");
        AuthData secondAuth = new AuthData("Secret Service", "James Bond");
        authDAO.createAuth(auth);
        authDAO.createAuth(secondAuth);

        authDAO.deleteAuth("On Her Majesty's");

        assertNull(authDAO.getAuth("On Her Majesty's"));
        assertEquals(secondAuth, authDAO.getAuth("Secret Service"));
    }

    @Test
    public void deleteMissingAuth() throws DataAccessException{
        AuthData auth = new AuthData("007", "Bond");
        authDAO.createAuth(auth);

        assertDoesNotThrow(() -> authDAO.deleteAuth("random-token"));
        assertEquals(auth, authDAO.getAuth("007"));
    }

    @Test
    public void clearAllAuths() throws DataAccessException{
        authDAO.createAuth(new AuthData("007", "James Bond"));
        authDAO.createAuth(new AuthData("008", "Bill Timothy"));
        authDAO.clear();

        assertNull(authDAO.getAuth("007"));
        assertNull(authDAO.getAuth("008"));
    }

    @Test
    public void clearEmptyTable() throws DataAccessException{
        assertDoesNotThrow(() -> authDAO.clear());

        AuthData auth = new AuthData("tokenZ", "James Bond?");
        authDAO.createAuth(auth);

        assertEquals(auth, authDAO.getAuth("tokenZ"));


    }

    @Test
    public void authPersistenceAcrossDAOInstances() throws DataAccessException{
        AuthData auth = new AuthData("007", "Sean Connery");
        authDAO.createAuth(auth);
        SQLAuthDAO anotherDAO = new SQLAuthDAO();

        assertEquals(auth, anotherDAO.getAuth("007"));
    }
}
