package com.adamschueller.chesswizard.engine.move;

import lombok.AllArgsConstructor;

import java.awt.*;

@AllArgsConstructor
public class Move {
    public Point startPos = new Point();
    public Point endPos = new Point();
    
    public Move(String moveString) {
        this.startPos.x = moveString.charAt(0) - 'a';
        this.startPos.y = moveString.charAt(1) - '0' - 1;
        this.endPos.x = moveString.charAt(2) - 'a';
        this.endPos.y = moveString.charAt(3) - '0' - 1;
    }

    @Override
    public String toString() {
        String startPos = String.format("%c%d", this.startPos.x + 'a', this.startPos.y + 1);
        String endPos = String.format("%c%d", this.endPos.x + 'a', this.endPos.y + 1);
        return startPos + endPos;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj instanceof Move) {
            Move move = (Move) obj;
            return this.startPos.equals(move.startPos) && this.endPos.equals(move.endPos);
        }
        return obj.equals(this);
    }
}
