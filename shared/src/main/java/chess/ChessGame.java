package chess;

import java.sql.Array;
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
        if (occupying == null){
            return null;
        }

        Collection<ChessMove> moves = occupying.pieceMoves(board, startPosition);
        Collection<ChessMove> legalMoves = new ArrayList<>();

        for (ChessMove move : moves){
            ChessPiece capturedPiece = board.getPiece(move.getEndPosition());
            board.addPiece(startPosition, null);
            board.addPiece(move.getEndPosition(), occupying);
            if (!isInCheck(occupying.getTeamColor())){
                legalMoves.add(move);
            }

            board.addPiece(startPosition, occupying);
            board.addPiece(move.getEndPosition(), capturedPiece);
        }
        return legalMoves;
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
    }

    /**
     * Determines if the given team is in check
     *
     * @param teamColor which team to check for check
     * @return True if the specified team is in check
     */
    public boolean isInCheck(TeamColor teamColor) {
        ChessPosition kingPosition = null;
        for (int outer = 1; outer <= 8; outer++){
            for (int inner = 1; inner <= 8; inner++){
                ChessPosition destination = new ChessPosition(outer, inner);
                ChessPiece occupying = board.getPiece(destination);
                if (occupying != null && occupying.getTeamColor() == teamColor && occupying.getPieceType() == ChessPiece.PieceType.KING){
                    kingPosition = destination;
                }
            }
        }

        for (int outer = 1; outer <= 8; outer++){
            for (int inner = 1; inner <= 8; inner++){
                ChessPosition destination = new ChessPosition(outer, inner);
                ChessPiece occupying = board.getPiece(destination);
                if (occupying != null && occupying.getTeamColor() != teamColor){
                    Collection<ChessMove> enemyMoves = occupying.pieceMoves(board, destination);
                    for (ChessMove move:enemyMoves){
                        if (move.getEndPosition().equals(kingPosition)){
                            return true;
                        }
                    }
                }
            }
        }

        return false;
    }

    /**
     * Determines if the given team is in checkmate
     *
     * @param teamColor which team to check for checkmate
     * @return True if the specified team is in checkmate
     */
    public boolean isInCheckmate(TeamColor teamColor) {
        if (!isInCheck(teamColor)){
            return false;
        }

        for (int outer = 1; outer <= 8; outer++){
            for (int inner = 1; inner <= 8; inner++){
                ChessPosition destination = new ChessPosition(outer, inner);
                ChessPiece occupying = board.getPiece(destination);
                if (occupying != null && occupying.getTeamColor() == teamColor){
                    Collection<ChessMove> moves = validMoves(destination);
                    if (!moves.isEmpty()){
                        return false;
                    }
                }
            }
        }

        return true;
    }

    /**
     * Determines if the given team is in stalemate, which here is defined as having
     * no valid moves while not in check.
     *
     * @param teamColor which team to check for stalemate
     * @return True if the specified team is in stalemate, otherwise false
     */
    public boolean isInStalemate(TeamColor teamColor) {
        throw new RuntimeException("Not implemented");
    }

    /**
     * Sets this game's chessboard to a given board
     *
     * @param board the new board to use
     */
    public void setBoard(ChessBoard board) {
        this.board = board;
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
