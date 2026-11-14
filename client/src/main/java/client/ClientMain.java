package client;

import chess.*;
import ui.BoardRenderer;
import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        new ChessClient(8080).run();
    }
}
