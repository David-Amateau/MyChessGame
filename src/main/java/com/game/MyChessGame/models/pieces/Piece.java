package com.game.MyChessGame.models.pieces;

import com.game.MyChessGame.models.board.Board;
import com.game.MyChessGame.models.board.Move;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Collection;


@Data
@NoArgsConstructor
@AllArgsConstructor
public abstract class Piece {

    protected int piecePosition;
    protected Alliance pieceAlliance;
    protected boolean isFirstMove;
    protected PieceType pieceType;

    Piece(final Alliance pieceAlliance, final int piecePosition, final PieceType pieceType) {
        this.piecePosition = piecePosition;
        this.pieceAlliance = pieceAlliance;
        this.pieceType = pieceType;

        //  TODO
        this.isFirstMove = false;
    }

    public abstract Collection<Move> calculateLegalMoves(final Board board);
}
