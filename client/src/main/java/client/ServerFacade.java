package client;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServerFacade {
    private final String baseURL;
    private final HttpClient httpClient = HttpClient.newHttpClient();

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
}
