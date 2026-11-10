package client;

import com.google.gson.Gson;
import request.RegisterRequest;
import result.RegisterResult;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String baseURL;
    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final Gson gson = new Gson();

    public ServerFacade(int port){
        this.baseURL = "http://localhost:" + port;
    }

    public void clear() throws IOException, InterruptedException {
        HttpRequest httpRequest = HttpRequest.newBuilder().uri(URI.create(baseURL + "/db")).DELETE().build();
        var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.discarding());
        if (response.statusCode() < 200 || response.statusCode() >= 300){
            throw new IOException("Failed to clear the test database");
        }
    }

    public RegisterResult register(String username, String password, String email)
            throws IOException, InterruptedException {
        var registration = new RegisterRequest(username, password, email);
        String json = gson.toJson(registration);

        var httpRequest = HttpRequest.newBuilder().uri(URI.create(baseURL + "/user"))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        var response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());

        if (response.statusCode() == 403) {
            throw new IOException("Username already taken.");
        }
        if (response.statusCode() < 200 || response.statusCode() >= 300) {
            throw new IOException("Could not register the user.");
        }

        return gson.fromJson(response.body(), RegisterResult.class);
    }


}
