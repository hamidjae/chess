package handler;

import service.ClearService;
import dataaccess.DataAccessException;
import io.javalin.http.Context;

public class ClearHandler {
    private final ClearService clearService;

    public ClearHandler(ClearService clearService){
        this.clearService = clearService;
    }

    public void handle(Context context) throws DataAccessException{
        clearService.clear();
        context.status(200);
        context.contentType("application/json");
        context.result("{}");
    }
}
