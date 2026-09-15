package chess;

import java.util.Collection;
import java.util.Objects;
import java.util.ArrayList;

/**
 * Represents a single chess piece
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessPiece {
    private final ChessGame.TeamColor pieceColor;
    private final ChessPiece.PieceType type;

    public ChessPiece(ChessGame.TeamColor pieceColor, ChessPiece.PieceType type) {
        this.pieceColor = pieceColor;
        this.type = type;
    }

    /**
     * The various different chess piece options
     */
    public enum PieceType {
        KING,
        QUEEN,
        BISHOP,
        KNIGHT,
        ROOK,
        PAWN
    }

    /**
     * @return Which team this chess piece belongs to
     */
    public ChessGame.TeamColor getTeamColor() {
        return pieceColor;
    }

    /**
     * @return which type of chess piece this piece is
     */
    public PieceType getPieceType() {
        return type;
    }

    /**
     * Calculates all the positions a chess piece can move to
     * Does not take into account moves that are illegal due to leaving the king in
     * danger
     *
     * @return Collection of valid moves
     */
    public Collection<ChessMove> pieceMoves(ChessBoard board, ChessPosition myPosition) {
        Collection<ChessMove> moves = new ArrayList<>();

        switch (type){
            case KING:
                for (int rowChanges = -1; rowChanges <= 1; rowChanges++){
                    for (int colChanges = -1; colChanges <= 1; colChanges++){
                        if (rowChanges == 0 && colChanges == 0){
                            continue;
                        }
                        int newRow = myPosition.getRow() + rowChanges;
                        int newCol = myPosition.getColumn() + colChanges;
                        if (newRow >= 1 && newRow <= 8 && newCol >= 1 && newCol <= 8){
                            ChessPosition destination = new ChessPosition(newRow, newCol);
                            ChessPiece occupying = board.getPiece(destination);

                            if (occupying == null || occupying.getTeamColor() != pieceColor){
                                moves.add(new ChessMove(myPosition, destination, null));
                            }
                        }
                    }
                }
                break;

            case ROOK:
                for (int rowChanges = myPosition.getRow() + 1; rowChanges <= 8; rowChanges++){
                    ChessPosition destination = new ChessPosition(rowChanges, myPosition.getColumn());
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null){
                        break;
                    }
                }
                for (int rowChanges = myPosition.getRow() - 1; rowChanges >= 1; rowChanges--) {
                    ChessPosition destination = new ChessPosition(rowChanges, myPosition.getColumn());
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                for (int colChanges = myPosition.getColumn() + 1; colChanges <= 8; colChanges++){
                    ChessPosition destination = new ChessPosition(myPosition.getRow(), colChanges);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                for (int colChanges = myPosition.getColumn() - 1; colChanges >= 1; colChanges--){
                    ChessPosition destination = new ChessPosition(myPosition.getRow(), colChanges);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                break;

            case BISHOP:
                int bisRow = myPosition.getRow() + 1;
                int bisCol = myPosition.getColumn() + 1;
                while (bisRow <= 8 && bisCol <= 8){
                    ChessPosition destination = new ChessPosition(bisRow, bisCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    bisRow++;
                    bisCol++;
                }

                bisRow = myPosition.getRow() + 1;
                bisCol = myPosition.getColumn() - 1;
                while (bisRow <= 8 && bisCol >= 1){
                    ChessPosition destination = new ChessPosition(bisRow, bisCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    bisRow++;
                    bisCol--;
                }

                bisRow = myPosition.getRow() - 1;
                bisCol = myPosition.getColumn() + 1;
                while (bisRow >= 1 && bisCol <= 8){
                    ChessPosition destination = new ChessPosition(bisRow, bisCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    bisRow--;
                    bisCol++;
                }

                bisRow = myPosition.getRow() - 1;
                bisCol = myPosition.getColumn() - 1;
                while (bisRow >= 1 && bisCol >= 1){
                    ChessPosition destination = new ChessPosition(bisRow, bisCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    bisRow--;
                    bisCol--;
                }
                break;

            case KNIGHT:
//                Up-right
                if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() + 1 <= 8){
                    ChessPosition destination = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() + 1);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Up-left
                if (myPosition.getRow() + 2 <= 8 && myPosition.getColumn() -1  >= 1){
                    ChessPosition destination = new ChessPosition(myPosition.getRow() + 2, myPosition.getColumn() - 1);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Right-up
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() + 2 <= 8){
                    ChessPosition destination = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() + 2);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Right-down
                if (myPosition.getRow() - 1 >= 1 && myPosition.getColumn() + 2 <= 8){
                    ChessPosition destination = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() + 2);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Down-right
                if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() + 1 <= 8) {
                    ChessPosition destination = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() + 1);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Down-left
                if (myPosition.getRow() - 2 >= 1 && myPosition.getColumn() - 1 >= 1) {
                    ChessPosition destination = new ChessPosition(myPosition.getRow() - 2, myPosition.getColumn() - 1);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Left-up
                if (myPosition.getRow() + 1 <= 8 && myPosition.getColumn() - 2 >= 1) {
                    ChessPosition destination = new ChessPosition(myPosition.getRow() + 1, myPosition.getColumn() - 2);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
//                Left-down
                if (myPosition.getRow() -1 >= 1 && myPosition.getColumn() - 2 >= 1) {
                    ChessPosition destination = new ChessPosition(myPosition.getRow() - 1, myPosition.getColumn() - 2);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                }
                break;
            case QUEEN:
                for (int rowChanges = myPosition.getRow() + 1; rowChanges <= 8; rowChanges++){
                    ChessPosition destination = new ChessPosition(rowChanges, myPosition.getColumn());
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor){
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null){
                        break;
                    }
                }
                for (int rowChanges = myPosition.getRow() - 1; rowChanges >= 1; rowChanges--) {
                    ChessPosition destination = new ChessPosition(rowChanges, myPosition.getColumn());
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                for (int colChanges = myPosition.getColumn() + 1; colChanges <= 8; colChanges++){
                    ChessPosition destination = new ChessPosition(myPosition.getRow(), colChanges);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                for (int colChanges = myPosition.getColumn() - 1; colChanges >= 1; colChanges--){
                    ChessPosition destination = new ChessPosition(myPosition.getRow(), colChanges);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                }
                int queenRow = myPosition.getRow() + 1;
                int queenCol = myPosition.getColumn() + 1;
                while (queenRow <= 8 && queenCol <= 8){
                    ChessPosition destination = new ChessPosition(queenRow, queenCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    queenRow++;
                    queenCol++;
                }

                queenRow = myPosition.getRow() + 1;
                queenCol = myPosition.getColumn() - 1;
                while (queenRow <= 8 && queenCol >= 1){
                    ChessPosition destination = new ChessPosition(queenRow, queenCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    queenRow++;
                    queenCol--;
                }

                queenRow = myPosition.getRow() - 1;
                queenCol = myPosition.getColumn() + 1;
                while (queenRow >= 1 && queenCol <= 8){
                    ChessPosition destination = new ChessPosition(queenRow, queenCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    queenRow--;
                    queenCol++;
                }

                queenRow = myPosition.getRow() - 1;
                queenCol = myPosition.getColumn() - 1;
                while (queenRow >= 1 && queenCol >= 1){
                    ChessPosition destination = new ChessPosition(queenRow, queenCol);
                    ChessPiece occupying = board.getPiece(destination);
                    if (occupying == null || occupying.getTeamColor() != pieceColor) {
                        moves.add(new ChessMove(myPosition, destination, null));
                    }
                    if (occupying != null) {
                        break;
                    }
                    queenRow--;
                    queenCol--;
                }
                break;
        }
        return moves;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessPiece that = (ChessPiece) o;
        return pieceColor == that.pieceColor && type == that.type;
    }

    @Override
    public int hashCode() {
        return Objects.hash(pieceColor, type);
    }
}
