package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class Knight extends Piece {
    public static final int SCORE = 3_000;

    public Knight(Color color, Point position, Board board) {
        super(color, position, board);
        this.notation = color == Color.WHITE ? 'N' : 'n';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        int moveX[] = new int[]{-1, 1, -2, 2};
        int moveY[] = new int[]{-1, 1, -2, 2};

        ArrayList<Move> moves = new ArrayList<>();
        for (int moveShort = 0; moveShort <= 1; moveShort++) {
            for (int moveLong = 2; moveLong <= 3; moveLong++) {
                Point endPosShortX = new Point(pos.x + moveX[moveShort], pos.y + moveY[moveLong]);
                Point endPosShortY = new Point(pos.x + moveX[moveLong], pos.y + moveY[moveShort]);
                if (board.isEmptyOrAttacking(this, endPosShortX)) moves.add(new Move(pos, endPosShortX));
                if (board.isEmptyOrAttacking(this, endPosShortY)) moves.add(new Move(pos, endPosShortY));
            }
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new Knight(color, pos, board);
    }
}
