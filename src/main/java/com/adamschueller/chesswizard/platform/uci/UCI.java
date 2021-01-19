package com.adamschueller.chesswizard.platform.uci;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.move.Move;

import java.util.Scanner;

import static com.adamschueller.chesswizard.engine.Engine.CHESS_WIZARD;

public class UCI {
    public void init() {
        System.out.println("ChessWizard 1.0 by Adam Schueller");
        System.out.println("id name ChessWizard 1.0");
        System.out.println("id author A. Schueller");
    }

    public void loop() {
        boolean quit = false;
        Scanner scanner = new Scanner(System.in);
        while (scanner.hasNext() && !quit) {
            String[] cmd = scanner.nextLine().split(" ");
            String token = cmd[0];

            if (token.equals("uci")) {
                System.out.println("uciok");
            } else if (token.equals("isready")) {
                System.out.println("readyok");
            } else if (token.equals("debug")) {
                // switch the debug mode of the engine on and off.
            } else if (token.equals("setoption")) {
                // this is sent to the engine when the user wants to change the internal parameters of the engine
            } else if (token.equals("register")) {
                // this is the command to try to register an engine or to tell the engine that registration will be done later.
            } else if (token.equals("ucinewgame")) {
                // this is sent to the engine when the next search (started with "position" and "go") will be from a different game.
            } else if (token.equals("position")) {
                if (cmd[1].equals("startpos")) CHESS_WIZARD.board = (new Board());
                else if (cmd[1].equals("fen")) CHESS_WIZARD.board = (new Board(new Board.FEN(cmd)));
                else CHESS_WIZARD.board.move(new Move(cmd[cmd.length - 1]));
            } else if (token.equals("go")) {
                System.out.println("bestmove " + CHESS_WIZARD.getBestMove(true));
            } else if (token.equals("ponderhit")) {
                // the user has played the expected move.
            } else if (token.equals("stop")) {
                // stop calculating as soon as possible
            } else if (token.equals("quit")) {
                System.exit(0);
            }
        }
    }
}
