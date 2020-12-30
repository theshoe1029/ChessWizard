package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class Queen extends Piece{
    public static final int SCORE = 9_000;

    public Queen(Color color, Point position, Board board) {
        super(color, position, board);
        this.notation = color == Color.WHITE ? 'Q' : 'q';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        ArrayList<Move> possibleMoves = new ArrayList<>();
        possibleMoves.addAll(getMovesPlus());
        possibleMoves.addAll(getMovesCross());
        return possibleMoves;
    }

    @Override
    public Piece clone() {
        return new Queen(color, pos, board);
    }
}
