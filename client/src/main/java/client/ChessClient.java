package ui;

import java.util.Locale;
import java.util.Scanner;

public class ChessClient {
    private final Scanner scanner = new Scanner(System.in);
    private boolean running = true;

    public void run() {
        System.out.println("Welcome to 240 Chess!");
        System.out.println("Type help to see your commands.");

        while (running) {
            System.out.print("[LOGGED_OUT] >>> ");

            if (!scanner.hasNextLine()) {
                break;
            }

            String input = scanner.nextLine().trim();

            if (input.isEmpty()) {
                continue;
            }

            handleCommand(input);
        }

        System.out.println("Goodbye!");
    }

    private void handleCommand(String input) {
        String[] parts = input.split("\\s+", 2);
        String command = parts[0].toLowerCase(Locale.ROOT);

        switch (command) {
            case "help" -> {
                if (parts.length > 1) {
                    System.out.println("Usage: help");
                } else {
                    printHelp();
                }
            }
            case "quit" -> {
                if (parts.length > 1) {
                    System.out.println("Usage: quit");
                } else {
                    running = false;
                }
            }
            default -> System.out.println(
                    "Unknown command. Type help for available commands.");
        }
    }

    private void printHelp() {
        System.out.println("""
                help - Show available commands
                quit - Exit the program
                """);
    }
}