package service;

import dataaccess.AuthDAO;
import dataaccess.DataAccessException;
import dataaccess.UserDAO;
import org.mindrot.jbcrypt.BCrypt;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import model.UserData;
import model.AuthData;
import java.util.UUID;

public class UserService {
    private final AuthDAO authDAO;
    private final UserDAO userDAO;

    public UserService(AuthDAO authDAO, UserDAO userDAO) {
        this.authDAO = authDAO;
        this.userDAO = userDAO;
    }

    public RegisterResult register(RegisterRequest registerRequest) throws BadRequestException, DataAccessException, AlreadyTakenException {
        if (registerRequest == null){
            throw new BadRequestException("Error: bad request");
        }

        if (registerRequest.username() == null || registerRequest.password() == null || registerRequest.email() == null){
            throw new BadRequestException("Error: bad request");
        }

        if (userDAO.getUser(registerRequest.username()) != null) {
            throw new AlreadyTakenException("Error: already taken");
        }

        String passwordHash = BCrypt.hashpw(registerRequest.password(), BCrypt.gensalt());
        UserData user = new UserData(registerRequest.username(), passwordHash, registerRequest.email());
        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, registerRequest.username());

        userDAO.createUser(user);
        authDAO.createAuth(auth);

        return new RegisterResult(registerRequest.username(), authToken);
    }

    public LoginResult login(LoginRequest loginRequest) throws BadRequestException, DataAccessException, UnauthorizedException{
        if (loginRequest == null || loginRequest.username() == null || loginRequest.password() == null){
            throw new BadRequestException("Error: bad request");
        }

        UserData user = userDAO.getUser(loginRequest.username());

        if (user == null || !(BCrypt.checkpw(loginRequest.password(), user.password()))){
            throw new UnauthorizedException("Error: unauthorized");
        }

        String authToken = UUID.randomUUID().toString();
        AuthData auth = new AuthData(authToken, loginRequest.username());

        authDAO.createAuth(auth);

        return new LoginResult(loginRequest.username(), authToken);
    }

    public void logout(String authToken) throws DataAccessException, UnauthorizedException{
        AuthData auth = authDAO.getAuth(authToken);
        if (auth == null){
            throw new UnauthorizedException("Error: unauthorized");
        }

        authDAO.deleteAuth(authToken);
    }
}
