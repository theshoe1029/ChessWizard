package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class Bishop extends Piece {
    public static final int SCORE = 3_000;

    public Bishop(Color color, Point position, Board board) {
        super(color, position, board);
        this.notation = color == Color.WHITE ? 'B' : 'b';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        return getMovesCross();
    }

    @Override
    public Piece clone() {
        return new Bishop(color, pos, board);
    }
}
