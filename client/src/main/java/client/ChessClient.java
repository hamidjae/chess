package ui;

import java.util.Scanner;
import client.ServerFacade;
import java.io.IOException;


public class ChessClient {
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    private final ServerFacade facade;
    private String authToken;

    public ChessClient(int port) {
        facade = new ServerFacade(port);
    }

    public void run() {
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type help to see your commands.");

        while (running) {
            if (authToken == null) {
                System.out.print("[LOGGED_OUT] >>> ");
            } else {
                System.out.print("[LOGGED_IN] >>> ");
            }

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            try {
                handleCommand(input);
            } catch (IllegalArgumentException exception) {
                System.out.println(exception.getMessage());
            } catch (IOException exception) {
                System.out.println(
                        "Could not complete the request. Check your details and server connection.");
            } catch (InterruptedException exception) {
                Thread.currentThread().interrupt();
                System.out.println("The client was interrupted.");
                running = false;
            }
        }

        System.out.println("Goodbye!");
    }

    private String[] requireArguments(
            String arguments, int expectedCount, String usage) {
        String[] values;

        if (arguments.isBlank()) {
            values = new String[0];
        } else {
            values = arguments.split("\\s+");
        }

        if (values.length != expectedCount) {
            throw new IllegalArgumentException("Usage: " + usage);
        }

        return values;
    }

    private void requireLoggedOut() {
        if (authToken != null) {
            throw new IllegalArgumentException(
                    "You are already logged in.");
        }
    }

    private void register(String arguments)
            throws IOException, InterruptedException {
        requireLoggedOut();

        String[] values = requireArguments(
                arguments, 3, "register <username> <password> <email>");

        var result = facade.register(values[0], values[1], values[2]);
        authToken = result.authToken();

        System.out.println("Logged in as " + result.username() + ".");
    }

    private void login(String arguments)
            throws IOException, InterruptedException {
        requireLoggedOut();

        String[] values = requireArguments(
                arguments, 2, "login <username> <password>");

        var result = facade.login(values[0], values[1]);
        authToken = result.authToken();

        System.out.println("Logged in as " + result.username() + ".");
    }

    private void handlePrelogin(String command, String arguments)
            throws IOException, InterruptedException {
        switch (command) {
            case "register" -> register(arguments);
            case "login" -> login(arguments);
            default -> System.out.println(
                    "Please log in or register. Type help for commands.");
        }
    }

    private void handlePostlogin(String command, String arguments)
            throws IOException, InterruptedException {
        switch (command) {
            case "logout" -> {
                requireArguments(arguments, 0, "logout");
                logout();
            }
            default -> System.out.println(
                    "Unknown command. Type help for available commands.");
        }
    }

    private void logout() throws IOException, InterruptedException {
        try {
            facade.logout(authToken);
        } catch (IOException exception) {
            if (!"Session is no longer valid.".equals(exception.getMessage())) {
                throw exception;
            }
        }

        authToken = null;
        System.out.println("Logged out.");
    }

    private void handleCommand(String input)
            throws IOException, InterruptedException {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase();
        String arguments = "";

        if (parts.length == 2) {
            arguments = parts[1].trim();
        }

        if (command.equals("help")) {
            requireArguments(arguments, 0, "help");
            printHelp();
        } else if (command.equals("quit")) {
            requireArguments(arguments, 0, "quit");
            running = false;
        } else if (authToken == null) {
            handlePrelogin(command, arguments);
        } else {
            handlePostlogin(command, arguments);
        }
    }

    private void printHelp() {
        if (authToken == null) {
            System.out.println("""
                register <username> <password> <email> - Create an account
                login <username> <password>            - Log in
                help                                   - Show commands
                quit                                   - Exit
                """);
        } else {
            System.out.println("""
            logout - Log out
            help   - Show commands
            quit   - Exit
            """);
        }
    }
}