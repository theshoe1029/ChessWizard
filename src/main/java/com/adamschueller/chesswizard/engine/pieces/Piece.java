package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class Piece {
    public char notation;
    public Point pos;
    public Color color;
    public int score = 0;
    protected Board board;

    public Piece() { this.notation = '-'; }

    public Piece(Color color, Point pos, Board board) {
        this.color = color;
        this.pos = pos;
        this.board = board;
    }

    public ArrayList<Move> getMoves() { return new ArrayList<>(); }

    protected ArrayList<Move> getMovesPlus() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = this.pos.x + 1; x < 8; x++) {
            Point posEnd = new Point(x, this.pos.y);
            if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
            if (endMove(posEnd)) break;
        }
        for (int x = this.pos.x - 1; x >= 0; x--) {
            Point posEnd = new Point(x, this.pos.y);
            if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
            if (endMove(posEnd)) break;
        }
        for (int y = this.pos.y + 1; y < 8; y++) {
            Point posEnd = new Point(this.pos.x, y);
            if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
            if (endMove(posEnd)) break;
        }
        for (int y = this.pos.y - 1; y >= 0; y--) {
            Point posEnd = new Point(this.pos.x, y);
            if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
            if (endMove(posEnd)) break;
        }
        return moves;
    }

    protected ArrayList<Move> getMovesCross() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = this.pos.x + 1; x < 8; x++) {
            int move = x - this.pos.x;
            int upY = this.pos.y + move;
            if (upY <= 7) {
                Point posEnd = new Point(x, upY);
                if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
                if (endMove(posEnd)) break;
            }
        }
        for (int x = this.pos.x + 1; x < 8; x++) {
            int move = x - this.pos.x;
            int downY = this.pos.y - move;
            if (downY >= 0) {
                Point posEnd = new Point(x, downY);
                if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
                if (endMove(posEnd)) break;
            }
        }
        for (int x = this.pos.x - 1; x >= 0; x--) {
            int move = this.pos.x - x;
            int upY = this.pos.y + move;
            if (upY <= 7) {
                Point posEnd = new Point(x, upY);
                if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
                if (endMove(posEnd)) break;
            }
        }
        for (int x = this.pos.x - 1; x >= 0; x--) {
            int move = this.pos.x - x;
            int downY = this.pos.y - move;
            if (downY >= 0) {
                Point posEnd = new Point(x, downY);
                if (board.isEmptyOrAttacking(this, posEnd)) moves.add(new Move(this.pos, posEnd));
                if (endMove(posEnd)) break;
            }
        }
        return moves;
    }

    private boolean endMove(Point posEnd) {
        if (board.isEmptyOrAttacking(this, posEnd)) return board.isAttacking(this, posEnd);
        else return true;
    }

    @Override
    public String toString() {
        return Character.toString(this.notation);
    }

    @Override
    public Piece clone() {
        return new Piece();
    }
}
