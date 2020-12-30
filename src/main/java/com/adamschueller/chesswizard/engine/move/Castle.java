package com.adamschueller.chesswizard.engine.move;

import lombok.AllArgsConstructor;

import java.awt.*;

public class Castle extends Move {
    public enum Side {W_QUEENSIDE, W_KINGSIDE, B_QUEENSIDE, B_KINGSIDE};

    public static Position W_QUEENSIDE_START = new Position(new Point(4, 0), new Point(0,0));
    public static Position W_KINGSIDE_START = new Position(new Point(4, 0), new Point(7,0));
    public static Position B_QUEENSIDE_START = new Position(new Point(4, 7), new Point(0,7));
    public static Position B_KINGSIDE_START = new Position(new Point(4, 7), new Point(7,7));

    public static Position W_QUEENSIDE_END = new Position(new Point(1, 0), new Point(2,0));
    public static Position W_KINGSIDE_END = new Position(new Point(6, 0), new Point(5,0));
    public static Position B_QUEENSIDE_END = new Position(new Point(1, 7), new Point(2,7));
    public static Position B_KINGSIDE_END = new Position(new Point(6, 7), new Point(5,7));

    public Side side;

    public Castle(Side side) {
        super(getStartPos(side).kingPos, getEndPos(side).kingPos);
        this.side = side;
    }

    public Castle(String strMove) {
        super(new Move(strMove).startPos, new Move(strMove).endPos);
        this.side = strToSide(strMove);
    }

    public Position getStartPos() {
        return getStartPos(this.side);
    }

    public Position getEndPos() {
        return getEndPos(this.side);
    }

    private static Side strToSide(String strMove) {
        Move move = new Move(strMove);
        if (move.startPos.equals(W_QUEENSIDE_START.kingPos) && move.endPos.equals(W_QUEENSIDE_END.kingPos))
            return Side.W_QUEENSIDE;
        else if (move.startPos.equals(W_KINGSIDE_START.kingPos) && move.endPos.equals(W_KINGSIDE_END.kingPos))
            return Side.W_KINGSIDE;
        if (move.startPos.equals(B_QUEENSIDE_START.kingPos) && move.endPos.equals(B_QUEENSIDE_END.kingPos))
            return Side.B_QUEENSIDE;
        else
            return Side.B_KINGSIDE;
    }

    private static Position getStartPos(Side side) {
        if (side == Side.W_QUEENSIDE) return W_QUEENSIDE_START;
        if (side == Side.W_KINGSIDE) return W_KINGSIDE_START;
        if (side == Side.B_QUEENSIDE) return B_QUEENSIDE_START;
        else return B_KINGSIDE_START;
    }

    private static Position getEndPos(Side side) {
        if (side == Side.W_QUEENSIDE) return W_QUEENSIDE_END;
        if (side == Side.W_KINGSIDE) return W_KINGSIDE_END;
        if (side == Side.B_QUEENSIDE) return B_QUEENSIDE_END;
        else return B_KINGSIDE_END;
    }

    @AllArgsConstructor
    public static class Position {
        public Point kingPos;
        public Point rookPos;
    }
}
