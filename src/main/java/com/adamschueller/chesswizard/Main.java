package com.adamschueller.chesswizard;

import com.adamschueller.chesswizard.platform.uci.UCI;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class Main {
    public static void main(String args[]) {
        if (args.length > 0) {
            if (args[0].equals("server")) {
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
