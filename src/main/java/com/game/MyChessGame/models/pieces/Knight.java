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
public class Knight extends Piece{

    private final static int JUMP_UP_ONE_SPACE_LEFT = -17;
    private final static int JUMP_UP_ONE_SPACE_RIGHT = -15;
    private final static int JUMP_LEFT_ONE_SPACE_UP = -10;
    private final static int JUMP_RIGHT_ONE_SPACE_UP = -6;
    private final static int JUMP_LEFT_ONE_SPACE_DOWN = 6;
    private final static int JUMP_RIGHT_ONE_SPACE_DOWN = 10;
    private final static int JUMP_DOWN_ONE_SPACE_LEFT = 15;
    private final static int JUMP_DOWN_ONE_SPACE_RIGHT = 17;

    private final static int[] CANDIDATE_MOVE_COORDINATES = {
            JUMP_UP_ONE_SPACE_LEFT, JUMP_RIGHT_ONE_SPACE_UP, JUMP_UP_ONE_SPACE_RIGHT, JUMP_LEFT_ONE_SPACE_UP,
            JUMP_LEFT_ONE_SPACE_DOWN, JUMP_RIGHT_ONE_SPACE_DOWN, JUMP_DOWN_ONE_SPACE_LEFT, JUMP_DOWN_ONE_SPACE_RIGHT
    };

    /**
     * To construct a knight, send in the knights alliance and the knightss tile coordinate on the board.
     *
     * @param pieceAlliance the alliance of the knight
     * @param piecePosition the tile coordinate the knight is placed on
     */
    public Knight(final Alliance pieceAlliance, final int piecePosition) {
        super(pieceAlliance, piecePosition, PieceType.KNIGHT);
    }

    @Override
    public Collection<Move> calculateLegalMoves(final Board board) {
        final List<Move> legalMoves = new ArrayList<>();

        for (final int currentCandidateDestinationOffset : Knight.CANDIDATE_MOVE_COORDINATES) {
            int candidateDestinationCoordinate = this.piecePosition + currentCandidateDestinationOffset;
            if (BoardUtils.isValidTileCoordinate(candidateDestinationCoordinate)) {
                if (isFirstColumnExclusion(this.piecePosition, currentCandidateDestinationOffset) ||
                    isSecondColumnExclusion(this.piecePosition, currentCandidateDestinationOffset) ||
                    isSeventhColumnExclusion(this.piecePosition, currentCandidateDestinationOffset) ||
                    isEighthColumnExclusion(this.piecePosition, currentCandidateDestinationOffset)) {
                    continue;
                }
                final Tile candidateDestinationTile = board.getTile(candidateDestinationCoordinate);
                if (!candidateDestinationTile.isTileOccupied()) {
                    legalMoves.add(new MajorPieceMove(board, this, candidateDestinationCoordinate));
                } else {
                    final Piece pieceAtDestination = candidateDestinationTile.getPiece();
                    final Alliance pieceAlliance = pieceAtDestination.getPieceAlliance();
                    if (this.pieceAlliance != pieceAlliance) {
                        legalMoves.add(new AttackMove
                                (board, this, candidateDestinationCoordinate, pieceAtDestination));
                    }
                }
            }
        }
        return ImmutableList.copyOf(legalMoves);
    }

    @Override
    public Knight movePiece(Move move) {
        return new Knight(move.getMovedPiece().getPieceAlliance(), move.getDestinationCoordinate());
    }

    // If a Knight is on the 1st column it cannot jump or move left
    private static boolean isFirstColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.A_FILE[currentPosition] &&
                (candidateOffset == JUMP_UP_ONE_SPACE_LEFT || candidateOffset == JUMP_LEFT_ONE_SPACE_UP ||
                candidateOffset == JUMP_LEFT_ONE_SPACE_DOWN || candidateOffset == JUMP_DOWN_ONE_SPACE_LEFT);
    }

    // If a Knight is on the 2nd column it cannot jump left
    private static boolean isSecondColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.B_FILE[currentPosition] && (candidateOffset == JUMP_LEFT_ONE_SPACE_UP ||
                candidateOffset == JUMP_LEFT_ONE_SPACE_DOWN);
    }

    // If a Knight is on the 7th column it cannot jump right
    private static boolean isSeventhColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.G_FILE[currentPosition] && (candidateOffset == JUMP_RIGHT_ONE_SPACE_UP ||
                candidateOffset == JUMP_RIGHT_ONE_SPACE_DOWN);
    }

    // If a Knight is on the 8th column it cannot jump or move right
    private static boolean isEighthColumnExclusion(final int currentPosition, final int candidateOffset) {
        return BoardUtils.H_FILE[currentPosition] && (candidateOffset == JUMP_UP_ONE_SPACE_RIGHT ||
                candidateOffset == JUMP_RIGHT_ONE_SPACE_UP || candidateOffset == JUMP_RIGHT_ONE_SPACE_DOWN ||
                candidateOffset == JUMP_DOWN_ONE_SPACE_RIGHT);
    }

    @Override
    public String toString() {
        return PieceType.KNIGHT.toString();
    }
}
