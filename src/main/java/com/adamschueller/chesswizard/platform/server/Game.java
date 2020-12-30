package com.adamschueller.chesswizard.platform.server;

import com.adamschueller.chesswizard.engine.Engine;

public class Game {
    public long lastMove = System.currentTimeMillis();
    public Engine engine = new Engine();
}
