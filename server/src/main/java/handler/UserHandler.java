package handler;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;
import service.AlreadyTakenException;
import service.BadRequestException;
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
        context.result(gson.toJson(result));
    }
}
