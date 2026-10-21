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

        switch (type) {
            case KING:
                addKingMoves(board, myPosition, moves);
                break;
            case QUEEN:
                addRookMoves(board, myPosition, moves);
                addBishopMoves(board, myPosition, moves);
                break;
            case ROOK:
                addRookMoves(board, myPosition, moves);
                break;
            case BISHOP:
                addBishopMoves(board, myPosition, moves);
                break;
            case KNIGHT:
                addKnightMoves(board, myPosition, moves);
                break;
            case PAWN:
                addPawnMoves(board, myPosition, moves);
                break;
        }

        return moves;
    }

    private boolean isOnBoard(int row, int col) {
        return row >= 1 && row <= 8 && col >= 1 && col <= 8;
    }

    private void addKingMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        for (int rowChange = -1; rowChange <= 1; rowChange++) {
            for (int colChange = -1; colChange <= 1; colChange++) {
                if (rowChange == 0 && colChange == 0) {
                    continue;
                }

                int row = start.getRow() + rowChange;
                int col = start.getColumn() + colChange;
                addSingleMove(board, start, moves, row, col);
            }
        }
    }

    private void addKnightMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        int row = start.getRow();
        int col = start.getColumn();

        addSingleMove(board, start, moves, row + 2, col + 1);
        addSingleMove(board, start, moves, row + 2, col - 1);
        addSingleMove(board, start, moves, row + 1, col + 2);
        addSingleMove(board, start, moves, row - 1, col + 2);
        addSingleMove(board, start, moves, row - 2, col + 1);
        addSingleMove(board, start, moves, row - 2, col - 1);
        addSingleMove(board, start, moves, row + 1, col - 2);
        addSingleMove(board, start, moves, row - 1, col - 2);
    }

    private void addSingleMove(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int row, int col) {
        if (!isOnBoard(row, col)) {
            return;
        }

        ChessPosition destination = new ChessPosition(row, col);
        ChessPiece occupying = board.getPiece(destination);

        if (occupying == null || occupying.getTeamColor() != pieceColor) {
            moves.add(new ChessMove(start, destination, null));
        }
    }

    private void addRookMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addSlidingMoves(board, start, moves, 1, 0);
        addSlidingMoves(board, start, moves, -1, 0);
        addSlidingMoves(board, start, moves, 0, 1);
        addSlidingMoves(board, start, moves, 0, -1);
    }

    private void addBishopMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        addSlidingMoves(board, start, moves, 1, 1);
        addSlidingMoves(board, start, moves, 1, -1);
        addSlidingMoves(board, start, moves, -1, 1);
        addSlidingMoves(board, start, moves, -1, -1);
    }

    // Follow one direction until reaching a piece or the edge of the board.
    private void addSlidingMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int rowChange, int colChange) {
        int row = start.getRow() + rowChange;
        int col = start.getColumn() + colChange;

        while (isOnBoard(row, col)) {
            ChessPosition destination = new ChessPosition(row, col);
            ChessPiece occupying = board.getPiece(destination);

            if (occupying == null || occupying.getTeamColor() != pieceColor) {
                moves.add(new ChessMove(start, destination, null));
            }

            if (occupying != null) {
                break;
            }

            row += rowChange;
            col += colChange;
        }
    }

    private void addPawnMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves) {
        int direction;
        int startingRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            direction = 1;
            startingRow = 2;
        } else {
            direction = -1;
            startingRow = 7;
        }

        addPawnForwardMoves(board, start, moves, direction, startingRow);
        addPawnCaptures(board, start, moves, direction);
    }

    private void addPawnForwardMoves(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int direction, int startingRow) {
        int row = start.getRow() + direction;
        int col = start.getColumn();

        if (!isOnBoard(row, col)) {
            return;
        }

        ChessPosition destination = new ChessPosition(row, col);
        if (board.getPiece(destination) != null) {
            return;
        }

        addPawnMove(start, destination, moves);

        // Both squares must be empty for the pawn's first double move.
        if (start.getRow() == startingRow) {
            ChessPosition doubleMove = new ChessPosition(start.getRow() + 2 * direction, col);

            if (board.getPiece(doubleMove) == null) {
                moves.add(new ChessMove(start, doubleMove, null));
            }
        }
    }

    private void addPawnCaptures(ChessBoard board, ChessPosition start, Collection<ChessMove> moves, int direction) {
        int row = start.getRow() + direction;

        for (int colChange = -1; colChange <= 1; colChange += 2) {
            int col = start.getColumn() + colChange;

            if (!isOnBoard(row, col)) {
                continue;
            }

            ChessPosition destination = new ChessPosition(row, col);
            ChessPiece occupying = board.getPiece(destination);

            if (occupying != null && occupying.getTeamColor() != pieceColor) {
                addPawnMove(start, destination, moves);
            }
        }
    }

    private void addPawnMove(ChessPosition start, ChessPosition destination, Collection<ChessMove> moves) {
        int promotionRow;

        if (pieceColor == ChessGame.TeamColor.WHITE) {
            promotionRow = 8;
        } else {
            promotionRow = 1;
        }

        if (destination.getRow() == promotionRow) {
            moves.add(new ChessMove(start, destination, PieceType.KNIGHT));
            moves.add(new ChessMove(start, destination, PieceType.QUEEN));
            moves.add(new ChessMove(start, destination, PieceType.BISHOP));
            moves.add(new ChessMove(start, destination, PieceType.ROOK));
        } else {
            moves.add(new ChessMove(start, destination, null));
        }
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
