package handler;

import com.google.gson.Gson;
import model.AuthData;
import request.LoginRequest;
import request.RegisterRequest;
import result.LoginResult;
import result.RegisterResult;
import service.AlreadyTakenException;
import service.BadRequestException;
import service.UnauthorizedException;
import service.UserService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class UserHandler {
    private final UserService userService;

    public UserHandler(UserService userService){
        this.userService = userService;
    }

    public void register(Context context) throws DataAccessException, BadRequestException, AlreadyTakenException {
        Gson gson = new Gson();
        RegisterRequest request = gson.fromJson(context.body(), RegisterRequest.class);
        RegisterResult result = userService.register(request);
        context.contentType("application/json");
        context.result(gson.toJson(result));
    }

    public void login(Context context) throws DataAccessException, BadRequestException, UnauthorizedException{
        Gson gson = new Gson();
        LoginRequest request = gson.fromJson(context.body(), LoginRequest.class);
        LoginResult result = userService.login(request);
        context.contentType("application/json");
        context.result(gson.toJson(result));
    }

    public void logout(Context context) throws DataAccessException, UnauthorizedException{
        String authorizationToken = context.header("authorization");
        userService.logout(authorizationToken);
        context.status(200);
        context.contentType("application/json");
        context.result("{}");
    }
}
