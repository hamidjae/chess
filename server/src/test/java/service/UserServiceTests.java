package service;

import dataaccess.*;
import model.AuthData;
import model.UserData;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestTemplate;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import static org.junit.jupiter.api.Assertions.*;

public class UserServiceTests {

//    Registration

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

//    Login

    @Test
    public void successfulLogin() throws DataAccessException, BadRequestException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);
        userDAO.createUser(new UserData("alia", "p0302", "alia@gmail.com"));

        LoginRequest loginRequest = new LoginRequest("alia", "p0302");
        LoginResult result = userService.login(loginRequest);

//        Checking login credentials
        assertNotNull(result.authToken());
        AuthData storedAuth = authDAO.getAuth(result.authToken());
        assertNotNull(storedAuth);
        assertEquals("alia", storedAuth.username());
    }

    @Test
    public void wrongPasswordLogin() throws DataAccessException, BadRequestException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);
        userDAO.createUser(new UserData("alia", "p0302", "alia@gmail.com"));

        LoginRequest loginRequest = new LoginRequest("alia", "WrongPasswordDummy");

//        Checking to see if the wrong password throws the Unauthorized error
        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void unknownUsernameLogin() throws DataAccessException, BadRequestException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);
        userDAO.createUser(new UserData("alia", "p0302", "alia@gmail.com"));

//        Checking to see if logging into someone who doesn't exist throws the Unauthorized error
        LoginRequest loginRequest = new LoginRequest("unknownUser", "p0302");
        assertThrows(UnauthorizedException.class, () -> userService.login(loginRequest));
    }

    @Test
    public void missingPasswordLogin() throws DataAccessException, BadRequestException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

//        Checking to see if logging in with a null password throws the BadRequest error
        LoginRequest loginRequest = new LoginRequest("alia", null);
        assertThrows(BadRequestException.class, () -> userService.login(loginRequest));
    }

//    Logout

    @Test
    public void successfulLogout() throws DataAccessException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        AuthData auth = new AuthData("testToken", "mama");
        authDAO.createAuth(auth);
        userService.logout(auth.authToken());

        assertNull(authDAO.getAuth(auth.authToken()));

    }

    @Test
    public void unknownTokenLogout() throws DataAccessException, UnauthorizedException{
        UserDAO userDAO = new MemoryUserDAO();
        AuthDAO authDAO = new MemoryAuthDAO();
        UserService userService = new UserService(authDAO, userDAO);

        assertThrows(UnauthorizedException.class, () -> userService.logout("unknownToken"));
    }

}
