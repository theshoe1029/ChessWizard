package com.adamschueller.chesswizard.engine.move;

import java.awt.*;

public class Promotion extends Move {
    public enum Type {KNIGHT, BISHOP, ROOK, QUEEN};

    public Type type;

    public Promotion(String moveString) {
        super(moveString.substring(0, 4));
        if (moveString.charAt(4) == 'n') this.type = Type.KNIGHT;
        else if (moveString.charAt(4) == 'b') this.type = Type.BISHOP;
        else if (moveString.charAt(4) == 'r') this.type = Type.ROOK;
        else if (moveString.charAt(4) == 'q') this.type = Type.QUEEN;
    }

    public Promotion(Point startPos, Point endPos, Type type) {
        super(startPos, endPos);
        this.type = type;
    }

    @Override
    public String toString() {
        String startPos = String.format("%c%d", this.startPos.x + 'a', this.startPos.y + 1);
        String endPos = String.format("%c%d", this.endPos.x + 'a', this.endPos.y + 1);
        String typeString = this.type == Type.KNIGHT ? "k" : "q";
        return startPos + endPos + typeString;
    }
}
