package client;

public class ServerFacade {
    private final String baseURL;

    public ServerFacade(int port){
        this.baseURL = "http://localhost:" + port;
    }
}
