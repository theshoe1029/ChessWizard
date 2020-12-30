package com.adamschueller.chesswizard.platform.server;

import com.adamschueller.chesswizard.engine.Engine;
import com.adamschueller.chesswizard.engine.pieces.Color;

public class Game {
    public long lastMove = System.currentTimeMillis();
    public Engine engine = new Engine();
    public Color playerColor = Color.randomColor();
}
