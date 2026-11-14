package client;

import chess.*;
import ui.BoardRenderer;
import ui.ChessClient;

public class ClientMain {
    public static void main(String[] args) {
//        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
//        System.out.println("♕ 240 Chess Client: " + piece);

        ChessClient client = new ChessClient();
        client.run();
    }
}
