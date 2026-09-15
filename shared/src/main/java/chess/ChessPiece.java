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
