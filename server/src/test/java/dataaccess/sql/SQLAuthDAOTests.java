package dataaccess.sql;

import dataaccess.DataAccessException;
import model.AuthData;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

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

}
