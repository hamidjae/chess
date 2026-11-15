package client;

import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
        int port = 8080;

        if (args.length > 1) {
            System.out.println("Usage: ClientMain [port]");
            return;
        }

        if (args.length == 1) {
            try {
                port = Integer.parseInt(args[0]);
            } catch (NumberFormatException exception) {
                System.out.println("The port must be a number.");
                return;
            }

            if (port < 1 || port > 65535) {
                System.out.println("Enter a port between 1 and 65535.");
                return;
            }
        }

        new ChessClient(port).run();
    }
}