package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.EnPassant;
import com.adamschueller.chesswizard.engine.move.Move;
import com.adamschueller.chesswizard.engine.move.Promotion;
import com.adamschueller.chesswizard.engine.move.Promotion.Type;

import java.awt.*;
import java.util.ArrayList;

public class Pawn extends Piece {
    public static final int SCORE = 1_000;

    public Pawn(Color color, Point pos, Board board) {
        super(color, pos, board);
        this.notation = color == Color.WHITE ? 'P' : 'p';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        boolean hasMoved = (color == Color.WHITE && this.pos.y != 1) || (color == Color.BLACK && this.pos.y != 6);
        ArrayList<Move> moves = new ArrayList<>();
        for (int y = 1; y <= (hasMoved ? 1 : 2); y++) {
            for (int x = -1; x <= 1; x++) {
                int newY = color == Color.WHITE ? pos.y + y : pos.y - y;
                Point endPos = new Point(pos.x + x, newY);
                if (isMoveValid(endPos)) {
                    if (color == Color.WHITE && newY == 7 || color == Color.BLACK && newY == 0) {
                        moves.add(new Promotion(pos, endPos, Type.KNIGHT));
                        moves.add(new Promotion(pos, endPos, Type.BISHOP));
                        moves.add(new Promotion(pos, endPos, Type.ROOK));
                        moves.add(new Promotion(pos, endPos, Type.QUEEN));
                    } else if (board.getEnPassant().equals(endPos)
                            && board.getState()[endPos.y][endPos.x].notation == '-') {
                        moves.add(new EnPassant(pos, endPos));
                    } else {
                        moves.add(new Move(pos, endPos));
                    }
                }
            }
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new Pawn(color, pos, board);
    }

    private boolean isMoveValid(Point endPos) {
        boolean validY = color == Color.WHITE && endPos.y == pos.y + 1
                || color == Color.BLACK && endPos.y == pos.y - 1;
        boolean validStartY = color == Color.WHITE && endPos.y == pos.y + 2
                || color == Color.BLACK && endPos.y == pos.y - 2;
        boolean isEmpty;
        if (board.contains(endPos)) {
            isEmpty = board.getState()[endPos.y][endPos.x].notation == '-';
        } else {
            isEmpty = false;
        }
        if (board.contains(endPos)) {
            if (validY) {
                if (endPos.x == pos.x - 1 || endPos.x == pos.x + 1)
                    return board.isAttacking(this, endPos) || board.getEnPassant().equals(endPos);
                else if (endPos.x == pos.x && isEmpty)
                    return true;
                else
                    return false;
            }
            else if (validStartY && endPos.x == pos.x && isEmpty)
                return true;
            else return false;
        } else return false;
    }
}
