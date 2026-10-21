package chess;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Objects;

/**
 * A class that can manage a chess game, making moves on a board
 * <p>
 * Note: You can add to this class, but you may not alter
 * signature of the existing methods.
 */
public class ChessGame {
    private TeamColor team;
    private ChessBoard board;
    private ChessMove lastMove;

    public ChessGame() {
        this.team = TeamColor.WHITE;
        this.board = new ChessBoard();
        this.board.resetBoard();
    }

    /**
     * @return Which team's turn it is
     */
    public TeamColor getTeamTurn() {
        return team;
    }

    /**
     * Sets which teams turn it is
     *
     * @param team the team whose turn it is
     */
    public void setTeamTurn(TeamColor team) {
        this.team = team;
    }

    /**
     * Enum identifying the 2 possible teams in a chess game
     */
    public enum TeamColor {
        WHITE,
        BLACK
    }

    /**
     * Gets all valid moves for a piece at the given location
     *
     * @param startPosition the piece to get valid moves for
     * @return Set of valid moves for requested piece, or null if no piece at
     * startPosition
     */
    public Collection<ChessMove> validMoves(ChessPosition startPosition) {
        ChessPiece occupying = board.getPiece(startPosition);
        if (occupying == null) {
            return null;
        }

        Collection<ChessMove> moves = new ArrayList<>(
                occupying.pieceMoves(board, startPosition));
        Collection<ChessMove> legalMoves = new ArrayList<>();

        addEnPassantMove(startPosition, occupying, moves);

        for (ChessMove move : moves) {
            if (keepsKingSafe(move, occupying)) {
                legalMoves.add(move);
            }
        }

        return legalMoves;
    }

    private void addEnPassantMove(ChessPosition start, ChessPiece occupying,
                                  Collection<ChessMove> moves) {
        if (occupying.getPieceType() != ChessPiece.PieceType.PAWN
                || lastMove == null) {
            return;
        }

        ChessPosition lastStart = lastMove.getStartPosition();
        ChessPosition lastEnd = lastMove.getEndPosition();
        ChessPiece lastPiece = board.getPiece(lastEnd);

        if (lastPiece == null
                || lastPiece.getPieceType() != ChessPiece.PieceType.PAWN
                || lastPiece.getTeamColor() == occupying.getTeamColor()) {
            return;
        }

        if (Math.abs(lastEnd.getRow() - lastStart.getRow()) != 2) {
            return;
        }

        if (lastEnd.getRow() != start.getRow()
                || Math.abs(lastEnd.getColumn() - start.getColumn()) != 1) {
            return;
        }

        int targetRow = (lastStart.getRow() + lastEnd.getRow()) / 2;
        ChessPosition destination =
                new ChessPosition(targetRow, lastEnd.getColumn());

        if (board.getPiece(destination) == null) {
            moves.add(new ChessMove(start, destination, null));
        }
    }

    // Called for potential moves or moves already checked
    private ChessPosition getCapturePosition(ChessMove move, ChessPiece occupying) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();

        boolean isPawn = occupying.getPieceType() == ChessPiece.PieceType.PAWN;
        boolean changesColumn = start.getColumn() != end.getColumn();
        boolean destinationEmpty = board.getPiece(end) == null;

        if (isPawn && changesColumn && destinationEmpty) {
            return new ChessPosition(start.getRow(), end.getColumn());
        }

        return end;
    }

    private boolean keepsKingSafe(ChessMove move, ChessPiece occupying) {
        ChessPosition start = move.getStartPosition();
        ChessPosition end = move.getEndPosition();
        ChessPosition capturePosition = getCapturePosition(move, occupying);
        ChessPiece capturedPiece = board.getPiece(capturePosition);

        board.addPiece(capturePosition, null);
        board.addPiece(start, null);
        board.addPiece(end, occupying);

        try {
            return !isInCheck(occupying.getTeamColor());
        } finally {
            // Restoring the board, even if an exception is thrown
            board.addPiece(start, occupying);
            board.addPiece(end, null);
            board.addPiece(capturePosition, capturedPiece);
        }
    }

    /**
     * Makes a move in the chess game
     *
     * @param move chess move to perform
     * @throws InvalidMoveException if move is invalid
     */
    public void makeMove(ChessMove move) throws InvalidMoveException {
        ChessPiece occupying = board.getPiece(move.getStartPosition());
        if (occupying == null || occupying.getTeamColor() != getTeamTurn()){
            throw new InvalidMoveException("No piece found, or the wrong team's turn");
        }

        Collection<ChessMove> moves = validMoves(move.getStartPosition());
        if (!moves.contains(move)){
            throw new InvalidMoveException("The collection does not contain the move");
        }

        ChessPosition capturePosition = getCapturePosition(move, occupying);
        board.addPiece(capturePosition, null);

        board.addPiece(move.getStartPosition(), null);
        if (move.getPromotionPiece() == null){
            board.addPiece(move.getEndPosition(), occupying);
        }
        else{
            ChessPiece promotionPiece = new ChessPiece(occupying.getTeamColor(), move.getPromotionPiece());
            board.addPiece(move.getEndPosition(), promotionPiece);
        }
        if (getTeamTurn() == TeamColor.WHITE){
            setTeamTurn(TeamColor.BLACK);
        }
        else{
            setTeamTurn(TeamColor.WHITE);
        }

        lastMove = move;
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = findKing(teamColor);

        if (kingPosition == null) {
            return false;
        }

        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece occupying = board.getPiece(position);

                if (occupying != null && occupying.getTeamColor() != teamColor) {
                    Collection<ChessMove> enemyMoves =
                            occupying.pieceMoves(board, position);

                    for (ChessMove move : enemyMoves) {
                        if (move.getEndPosition().equals(kingPosition)) {
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    private ChessPosition findKing(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece occupying = board.getPiece(position);

                if (occupying != null
                        && occupying.getTeamColor() == teamColor
                        && occupying.getPieceType() == ChessPiece.PieceType.KING) {
                    return position;
                }
            }
        }

        return null;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)) {
            return false;
        }

        return !hasLegalMoves(teamColor);
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        if (isInCheck(teamColor)) {
            return false;
        }

        return !hasLegalMoves(teamColor);
    }

    private boolean hasLegalMoves(TeamColor teamColor) {
        for (int row = 1; row <= 8; row++) {
            for (int col = 1; col <= 8; col++) {
                ChessPosition position = new ChessPosition(row, col);
                ChessPiece occupying = board.getPiece(position);

                if (occupying != null && occupying.getTeamColor() == teamColor) {
                    Collection<ChessMove> moves = validMoves(position);

                    if (!moves.isEmpty()) {
                        return true;
                    }
                }
            }
        }

        return false;
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
        this.lastMove = null;
    }

    /**
     * Gets the current chessboard
     *
     * @return the chessboard
     */
    public ChessBoard getBoard() {
        return board;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ChessGame chessGame = (ChessGame) o;
        return team == chessGame.team && Objects.equals(getBoard(), chessGame.getBoard());
    }

    @Override
    public int hashCode() {
        return Objects.hash(team, getBoard());
    }
}
