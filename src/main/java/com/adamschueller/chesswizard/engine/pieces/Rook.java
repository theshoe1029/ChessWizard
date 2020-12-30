package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class Rook extends Piece {
    public static final int SCORE = 5_000;

    public Rook(Color color, Point position, Board board) {
        super(color, position, board);
        this.notation = color == Color.WHITE ? 'R' : 'r';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        return getMovesPlus();
    }

    @Override
    public Piece clone() {
        return new Rook(color, pos, board);
    }
}
