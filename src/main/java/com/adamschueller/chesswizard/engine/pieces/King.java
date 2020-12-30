package com.adamschueller.chesswizard.engine.pieces;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Castle;
import com.adamschueller.chesswizard.engine.move.Castle.Side;
import com.adamschueller.chesswizard.engine.move.Move;

import java.awt.*;
import java.util.ArrayList;

public class King extends Piece {
    public static final int SCORE = 100_000;

    public King(Color color, Point position, Board board) {
        super(color, position, board);
        this.notation = color == Color.WHITE ? 'K' : 'k';
        this.score = SCORE;
    }

    @Override
    public ArrayList<Move> getMoves() {
        ArrayList<Move> moves = new ArrayList<>();
        for (int x = -1; x <= 1; x++) {
            for (int y = -1; y <= 1; y++) {
                Point endPos = new Point(pos.x + x, pos.y + y);
                if (board.isEmptyOrAttacking(this, endPos) && !inCheck(endPos))
                    moves.add(new Move(pos, endPos));
            }
        }
        if (!inCheck(pos)) {
            if (color == Color.WHITE) {
                if (canCastle(Side.W_QUEENSIDE)) moves.add(new Castle(Side.W_QUEENSIDE));
                if (canCastle(Side.W_KINGSIDE)) moves.add(new Castle(Side.W_KINGSIDE));
            } else {
                if (canCastle(Side.B_QUEENSIDE)) moves.add(new Castle(Side.B_QUEENSIDE));
                if (canCastle(Side.B_KINGSIDE)) moves.add(new Castle(Side.B_KINGSIDE));
            }
        }
        return moves;
    }

    @Override
    public Piece clone() {
        return new King(color, pos, board);
    }

    public boolean inCheck() {
        return inCheck(pos);
    }

    private boolean inCheck(Point endPos) {
        ArrayList<Piece> opponent = board.getTeam(color == Color.WHITE ? Color.BLACK : Color.WHITE);
        for (Piece piece : opponent) {
            if (!(piece instanceof King)) {
                for (Move move : piece.getMoves()) {
                    if (move.endPos.equals(endPos)) return true;
                }
           }
        }
        return false;
    }

    private boolean canCastle(Side side) {
        Piece[][] state = board.getState();
        Castle castle = new Castle(side);
        Point kingEndPos = castle.getStartPos().kingPos;
        Point rookPos = castle.getStartPos().rookPos;
        if (!inCheck(new Point(kingEndPos.x, kingEndPos.y))) {
            switch(side) {
                case W_QUEENSIDE:
                    return board.getCastleState().wQueenside
                            && state[rookPos.y][rookPos.x].notation == 'R'
                            && state[rookPos.y][rookPos.x+1].notation == '-'
                            && state[rookPos.y][rookPos.x+2].notation == '-'
                            && state[rookPos.y][rookPos.x+3].notation == '-';
                case W_KINGSIDE:
                    return board.getCastleState().wKingside
                            && state[rookPos.y][rookPos.x-2].notation == '-'
                            && state[rookPos.y][rookPos.x-1].notation == '-'
                            && state[rookPos.y][rookPos.x].notation == 'R';
                case B_QUEENSIDE:
                    return board.getCastleState().bQueenside
                            && state[rookPos.y][rookPos.x].notation == 'r'
                            && state[rookPos.y][rookPos.x+1].notation == '-'
                            && state[rookPos.y][rookPos.x+2].notation == '-'
                            && state[rookPos.y][rookPos.x+3].notation == '-';
                case B_KINGSIDE:
                    return board.getCastleState().bKingside
                            && state[rookPos.y][rookPos.x-2].notation == '-'
                            && state[rookPos.y][rookPos.x-1].notation == '-'
                            && state[rookPos.y][rookPos.x].notation == 'r';
            }
            return false;
        } else return false;
    }
}
