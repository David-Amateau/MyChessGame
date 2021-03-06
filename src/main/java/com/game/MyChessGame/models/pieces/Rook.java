package com.game.MyChessGame.models.pieces;

import com.game.MyChessGame.models.board.*;
import com.game.MyChessGame.models.board.move.AttackMove;
import com.game.MyChessGame.models.board.move.MajorPieceMove;
import com.game.MyChessGame.models.board.move.Move;
import com.google.common.collect.ImmutableList;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Data
@NoArgsConstructor
public class Rook extends Piece{

    private final static int ONE_SPACE_DOWN = 8;
    private final static int ONE_SPACE_UP = -8;
    private final static int ONE_SPACE_LEFT = -1;
    private final static int ONE_SPACE_RIGHT = 1;

    private final static int[] CANDIDATE_MOVE_COORDINATES = {
            ONE_SPACE_DOWN, ONE_SPACE_UP, ONE_SPACE_LEFT, ONE_SPACE_RIGHT
    };

    /**
     * To construct a rook, send in the rooks alliance and the rooks tile coordinate on the board.
     *
     * @param pieceAlliance the alliance of the rook
     * @param piecePosition the tile coordinate the rook is placed on
     */
    public Rook(final Alliance pieceAlliance, final int piecePosition) {
        super(pieceAlliance, piecePosition, PieceType.ROOK);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for(final int currentCandidateDestinationOffset : Rook.CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition;
            while(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if(isFirstColumnExclusion(candidateDestinationCoordinate, currentCandidateDestinationOffset) ||
                        isEighthColumnExclusion(candidateDestinationCoordinate, currentCandidateDestinationOffset)) {
                    break;
                }
                candidateDestinationCoordinate += currentCandidateDestinationOffset;
                if(BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                    final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                    if(!candidateDestinationTile.isTileOccupied()) {
                        legalMoves.add(new MajorPieceMove(board, this, candidateDestinationCoordinate));
                    } else {  // If a Rook is blocked by any piece it cannot consider pieces past it
                        final Piece pieceAtDestination  = candidateDestinationTile.getPiece();
                        final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                        if (this.pieceAlliance != pieceAlliance) {
                            legalMoves.add(new AttackMove
                                    (board, this, candidateDestinationCoordinate, pieceAtDestination));
                        }
                        break;
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Rook movePiece(Move move) {
        return new Rook(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    // If a Rook is on the 1st column it cannot move left
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.A_FILE[currentPosition] && (candidateOffset == ONE_SPACE_LEFT);
    }

    // If a rook is on the 8th column it cannot move right
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == ONE_SPACE_RIGHT);
    }

    @Override
    public String toString() {
        return PieceType.ROOK.toString();
    }
}
