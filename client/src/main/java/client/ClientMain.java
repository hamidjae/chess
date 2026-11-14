package client;

import chess.*;
import ui.BoardRenderer;

public class ClientMain {
    public static void main(String[] args) {
        var piece = new ChessPiece(ChessGame.TeamColor.WHITE, ChessPiece.PieceType.PAWN);
        System.out.println("♕ 240 Chess Client: " + piece);

//        System.out.println("White perspective:");
//        BoardRenderer.drawInitialBoard(false);
//
//        System.out.println();
//
//        System.out.println("Black perspective:");
//        BoardRenderer.drawInitialBoard(true);
    }
}
