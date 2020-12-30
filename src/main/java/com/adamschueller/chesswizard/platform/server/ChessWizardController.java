package com.adamschueller.chesswizard.platform.server;

import com.adamschueller.chesswizard.engine.Board;
import com.adamschueller.chesswizard.engine.Engine;
import com.adamschueller.chesswizard.engine.move.Castle;
import com.adamschueller.chesswizard.engine.move.Move;
import com.adamschueller.chesswizard.engine.move.Promotion;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.boot.jackson.JsonComponent;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.ModelAndView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.UUID;

@Controller
public class ChessWizardController {
    private static final long TIMEOUT = 60_000;
    private int[] record = getRecord();
    private static Map<String, Game> games = new HashMap<>();

    public static void clearInactiveGames() {
        Iterator<Entry<String, Game> > iterator = games.entrySet().iterator();
        while (iterator.hasNext()) {
            Map.Entry<String, Game> entry = iterator.next();
            Game game = entry.getValue();
            long lastMoveTime = System.currentTimeMillis() - game.lastMove;
            if (lastMoveTime > TIMEOUT) {
                System.out.println(entry.getKey() + " timed out");
                iterator.remove();
            }
        }
    }

    @GetMapping("/")
    public ModelAndView index() {
        return new ModelAndView("index");
    }

    @ResponseBody
    @RequestMapping(value="/start", method=RequestMethod.POST)
    public StartResponse start() {
        UUID uuid = UUID.randomUUID();
        Game game = new Game(record);
        games.put(uuid.toString(), game);
        System.out.println(uuid.toString() + " connected");
        return new StartResponse(uuid, game);
    }

    @ResponseBody
    @RequestMapping(value="/restart", method=RequestMethod.POST)
    public RestartResponse restart(@RequestBody RestartRequest restartRequest) {
        Game game = new Game(record);
        games.replace(restartRequest.uuid, game);
        return new RestartResponse(game);
    }

    @ResponseBody
    @RequestMapping(value="/get", method=RequestMethod.POST)
    public GetResponse get(@RequestBody GetRequest getRequest) {
        try {
            Game game = games.get(getRequest.uuid);
            synchronized (game) {
                return new GetResponse(game);
            }
        }
        catch (NullPointerException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game with uuid " + getRequest.uuid + " not found");
        }
    }

    @ResponseBody
    @RequestMapping(value="/validate", method=RequestMethod.POST)
    private ValidateResponse validate(@RequestBody  ValidateRequest validateRequest) {
        try {
            Game game = games.get(validateRequest.uuid);
            game.lastMove = System.currentTimeMillis();

            Board board = game.engine.board;
            boolean valid = board.isMoveValid(validateRequest.move);
            boolean canPromote = board.canPromote(validateRequest.move);
            return new ValidateResponse(valid, canPromote);
        }
        catch (NullPointerException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game with uuid " + validateRequest.uuid + " not found");
        }
    }

    @ResponseBody
    @RequestMapping(value="/move", method=RequestMethod.POST)
    public MoveResponse move(@RequestBody MoveRequest moveRequest) {
        try {
            Move move;
            Game game = games.get(moveRequest.uuid);
            Board board = game.engine.board;

            if (moveRequest.move.length() == 5) move = new Promotion(moveRequest.move);
            else if (board.isCastle(moveRequest.move)) move = new Castle(moveRequest.move);
            else move = new Move(moveRequest.move);
            board.move(move);
            board.updateGameOver();
            try { if (board.isGameOver()) updateRecord(game); }
            catch (IOException exc) {
                System.out.println("Could not update record.txt");
                return new MoveResponse(game);
            }
            return new MoveResponse(game);
        }
        catch (NullPointerException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game with uuid " + moveRequest.uuid + " not found");
        }
    }

    @ResponseBody
    @RequestMapping(value="/go", method=RequestMethod.POST)
    public GoResponse go(@RequestBody GoRequest goRequest) {
        try {
            Game game = games.get(goRequest.uuid);
            Engine engine = game.engine;
            Board board = engine.board;

            synchronized (game) {
                board.move(engine.getBestMove(false));
                board.updateGameOver();
                try { if (board.isGameOver()) updateRecord(game); }
                catch (IOException exc) {
                    System.out.println("Could not update record.txt");
                    return new GoResponse(game);
                }
                return new GoResponse(game);
            }
        }
        catch (NullPointerException exc) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
                    "Game with uuid " + goRequest.uuid + " not found");
        }
    }

    @JsonComponent
    static class ValidateRequest {
        @Getter String uuid;
        @Getter Move move;
    }

    @AllArgsConstructor
    static class ValidateResponse {
        @Getter boolean valid;
        @Getter boolean promotion;
    }

    @AllArgsConstructor
    static class StartResponse {
        @Getter UUID uuid;
        @Getter Game game;
    }

    @JsonComponent
    static class RestartRequest {
        @Getter String uuid;
    }

    @AllArgsConstructor
    static class RestartResponse {
        @Getter Game game;
    }

    @JsonComponent
    static class GetRequest {
        @Getter String uuid;
    }

    @AllArgsConstructor
    static class GetResponse {
        @Getter Game game;
    }

    @JsonComponent
    static class MoveRequest {
        @Getter String uuid;
        @Getter String move;
    }

    @AllArgsConstructor
    static class MoveResponse {
        @Getter Game game;
    }

    @JsonComponent
    static class GoRequest {
        @Getter String uuid;
    }

    @AllArgsConstructor
    static class GoResponse {
        @Getter Game game;
    }

    private int[] getRecord() {
        int wins = 0;
        int losses = 0;
        File record = new File("./record.txt");
        try {
            Scanner recordScanner = new Scanner(record);
            while (recordScanner.hasNextLine()) {
                String data = recordScanner.nextLine();
                String[] winsAndLosses = data.split("-");
                wins = Integer.parseInt(winsAndLosses[0]);
                losses = Integer.parseInt(winsAndLosses[1]);
            }
            recordScanner.close();
        } catch (IOException exc) {
            System.out.println(exc);
            System.out.println("Could not read record.txt");
        }
        return new int[]{wins, losses};
    }

    private void updateRecord(Game game) throws IOException {
        int wins = this.record[0];
        int losses = this.record[1];

        if (game.engine.board.getTurn().equals(game.playerColor)) wins++;
        else losses++;

        this.record[0] = wins;
        this.record[1] = losses;

        String strRecord = wins + "-" + losses;
        FileWriter recordWriter = new FileWriter("./record.txt");
        recordWriter.write(strRecord);
        recordWriter.close();
    }
}
