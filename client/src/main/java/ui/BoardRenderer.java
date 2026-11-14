package ui;

import chess.ChessBoard;
import chess.ChessGame;
import chess.ChessPiece;
import chess.ChessPosition;

import static ui.EscapeSequences.*;

public class BoardRenderer {
    public static void drawInitialBoard(boolean blackPerspective) {
        ChessGame game = new ChessGame();
        ChessBoard board = game.getBoard();

        printBorder(blackPerspective);

        for (int rowIndex = 0; rowIndex < 8; rowIndex++) {
            int row;
            if (blackPerspective) {
                row = rowIndex + 1;
            }
            else {
                row = 8 - rowIndex;
            }

            printRowNumber(row);

            for (int columnIndex = 0; columnIndex < 8; columnIndex++) {
                int column;
                if (blackPerspective) {
                    column = 8 - columnIndex;
                }
                else {
                    column = columnIndex + 1;
                }

                if ((row + column) % 2 == 0) {
                    System.out.print(SET_BG_COLOR_BLACK);
                }
                else {
                    System.out.print(SET_BG_COLOR_WHITE);
                }

                ChessPosition position = new ChessPosition(row, column);
                ChessPiece piece = board.getPiece(position);
                System.out.print(pieceText(piece));
            }

            printRowNumber(row);
            System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
        }

        printBorder(blackPerspective);
    }

    private static void printRowNumber(int row) {
        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print(" " + row + " ");
    }

    private static void printBorder(boolean blackPerspective) {
        String columns;

        if (blackPerspective) {
            columns = " h  g  f  e  d  c  b  a ";
        }
        else {
            columns = " a  b  c  d  e  f  g  h ";
        }

        System.out.print(SET_BG_COLOR_DARK_GREY);
        System.out.print(SET_TEXT_COLOR_WHITE);
        System.out.print("   " + columns + "   ");
        System.out.println(RESET_BG_COLOR + RESET_TEXT_COLOR);
    }

    private static String pieceText(ChessPiece piece) {
        if (piece == null) {
            return "   ";
        }

        String color;
        if (piece.getTeamColor() == ChessGame.TeamColor.WHITE) {
            color = SET_TEXT_COLOR_RED;
        }
        else {
            color = SET_TEXT_COLOR_BLUE;
        }

        String symbol = switch (piece.getPieceType()) {
            case KING -> "K";
            case QUEEN -> "Q";
            case ROOK -> "R";
            case BISHOP -> "B";
            case KNIGHT -> "N";
            case PAWN -> "P";
        };

        return color + " " + symbol + " ";
    }
}