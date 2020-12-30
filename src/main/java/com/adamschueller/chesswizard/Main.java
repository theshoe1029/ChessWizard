package com.adamschueller.chesswizard;

import com.adamschueller.chesswizard.platform.server.ChessWizardController;
import com.adamschueller.chesswizard.platform.uci.UCI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.Timer;
import java.util.TimerTask;

@SpringBootApplication
public class Main {
    static Timer timer = new Timer();
    static TimerTask clearInactiveGames = new TimerTask() {
        @Override
        public void run() {
            ChessWizardController.clearInactiveGames();
        }
    };

    public static void main(String args[]) {
        if (args.length > 0) {
            if (args[0].equals("server")) {
                timer.schedule(clearInactiveGames, 60_000, 60_000);
                SpringApplication.run(Main.class, args);
            } else {
                startLocal();
            }
        } else {
            startLocal();
        }
    }

    public static void startLocal() {
        UCI uci = new UCI();
        uci.init();
        uci.loop();
    }
}
