package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import request.RegisterRequest;
import result.RegisterResult;

import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {
    @Test
    public void checkRegistration() throws DataAccessException, BadRequestException, AlreadyTakenException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest registerRequest = new RegisterRequest("hamid", "BYUrocks67", "hamidjjahangir@gmail.com");
        RegisterResult result = userService.register(registerRequest);

//        Checking if the user exists
        assertEquals("hamid", result.username());
        UserData storedUser = userDAO.getUser("hamid");
        assertNotNull(storedUser);

//        Checking if the authToken exists
        AuthData storedAuth = authDAO.getAuth(result.authToken());
        assertNotNull(storedAuth);
        assertEquals("hamid", storedAuth.username());
    }

    @Test
    public void alreadyTakenRegistration() throws DataAccessException, BadRequestException, AlreadyTakenException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        RegisterRequest registerRequest = new RegisterRequest("hamid", "cougareats", "hamidjae@byu.edu");
        RegisterResult result = userService.register(registerRequest);

//        Checking to see if the username already exists
        assertThrows(AlreadyTakenException.class, () -> userService.register(registerRequest));
    }

    @Test
    public void nullRegistrations(){
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        assertThrows(BadRequestException.class, () -> userService.register(new RegisterRequest(null, "jacquelinesZ1!", "jackie@gmail.com")));
        assertThrows(BadRequestException.class, () -> userService.register(new RegisterRequest("jacqueline", null, "jackie@gmail.com")));
        assertThrows(BadRequestException.class, () -> userService.register(new RegisterRequest("jacqueline", "jacquelinesZ1!", null)));
        assertThrows(BadRequestException.class, () -> userService.register(null));
    }
}
