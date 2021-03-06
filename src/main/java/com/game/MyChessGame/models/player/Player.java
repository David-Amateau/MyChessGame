package com.game.MyChessGame.models.player;

import com.game.MyChessGame.models.board.Board;
import com.game.MyChessGame.models.board.move.Move;
import com.game.MyChessGame.models.pieces.Alliance;
import com.game.MyChessGame.models.pieces.King;
import com.game.MyChessGame.models.pieces.Piece;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public abstract class Player {

    protected Board board;
    protected King playerKing;
    protected Collection<Move> playersLegalMoves;
    private boolean isInCheck;

    Player(final Board board, final Collection<Move> playersLegalMoves, final Collection<Move> opponentsLegalMoves) {
        this.board = board;
        this.playerKing = establishKing();
        this.playersLegalMoves = ImmutableList.copyOf(Iterables.concat(playersLegalMoves,
                calculateCastlingLegalMoves(playersLegalMoves, opponentsLegalMoves)));
        this.isInCheck = !Player.calculateAttacksOnTile(this.playerKing.getPiecePosition(),
                opponentsLegalMoves).isEmpty();
    }

    private King establishKing() {
        for(Piece piece : getActivePieces()) {
            if (piece.getPieceType().isKing()) {
                return (King) piece;
            }
        }
        throw new RuntimeException("The state of this board is invalid.");
    }

    public boolean isMoveLegal(final Move move) {
        return this.playersLegalMoves.contains(move);
    }

    public boolean isInCheck() {
        return this.isInCheck;
    }

    protected static Collection<Move> calculateAttacksOnTile(int piecePosition, Collection<Move> moves) {
        final List<Move> attackMoves = new ArrayList<>();
        for (final Move move : moves) {
            if (piecePosition == move.getDestinationCoordinate()) {
                attackMoves.add(move);
            }
        }
        return ImmutableList.copyOf(attackMoves);
    }

    public boolean isInCheckMate() {
        return this.isInCheck && !hasEscapeMoves();
    }

    protected boolean hasEscapeMoves() {
        for (final Move move : this.playersLegalMoves) {
            final MoveTransition transition = makeMove(move);
            if (transition.getMoveStatus().isDone()) {
                return true;
            }
        }
        return false;
    }

    public boolean isInStaleMate() {
        return !this.isInCheck && !hasEscapeMoves();
    }

    public boolean isCastled() {
        return false;
    }

    public MoveTransition makeMove(final Move move) {
        if (!isMoveLegal(move)) {
            return new MoveTransition(this.board, move, MoveStatus.ILLEGAL_MOVE);
        }
        final Board transitionBoard = move.execute();
        final Collection<Move> kingAttacks = Player.calculateAttacksOnTile
                (transitionBoard.getCurrentPlayer().getOpponent().getPlayerKing().getPiecePosition(),
                        transitionBoard.getCurrentPlayer().getPlayersLegalMoves());

        if (!kingAttacks.isEmpty()) {
            return new MoveTransition(this.board, move, MoveStatus.LEAVES_PLAYER_IN_CHECK);
        }
        return new MoveTransition(transitionBoard, move, MoveStatus.DONE);
    }

    public abstract Collection<Piece> getActivePieces();
    public abstract Alliance getAlliance();
    public abstract Player getOpponent();
    protected abstract Collection<Move> calculateCastlingLegalMoves(Collection<Move> playersLegalMoves,
                                                                    Collection<Move> opponentsLegalMoves);

}
