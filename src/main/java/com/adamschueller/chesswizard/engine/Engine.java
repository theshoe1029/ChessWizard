package com.adamschueller.chesswizard.engine;

import com.adamschueller.chesswizard.engine.Board.CastleState;
import com.adamschueller.chesswizard.engine.move.Move;
import com.adamschueller.chesswizard.engine.pieces.Color;
import com.adamschueller.chesswizard.engine.pieces.Piece;

import java.awt.*;
import java.util.PriorityQueue;

public class Engine {
    public static Engine CHESS_WIZARD = new Engine();
    public Board board = new Board();

    //position fen r1bqk2r/1ppp1p1p/4pn1b/p2Pn1p1/4P1P1/2P1BP2/PP5P/RN1QKBNR w KQkq - 0 1
    //position startpos move d2d4 f7f5 c1f4 f4c7 d8c7
    //promote pawn
    //position fen 8/7P/8/8/8/8/8/7q w KQkq - 0 1
    //castle queenside
    //position fen R3K3/8/8/8/8/8/8/8 w KQkq - 0 1
    //castle kingside
    //position fen 4K2R/8/8/8/8/8/8/8 w KQkq - 0 1
    //can't castle out of check
    //position fen R3K3/8/8/8/8/8/8/4q3 w KQkq - 0 1
    //can't castle into check
    //position fen R3K3/8/8/8/8/8/8/1q6 w KQkq - 0 1
    //en passant
    //position fen 8/8/8/8/1p6/8/P7/8 w KQkq - 0 1
    public Move getBestMove(boolean shouldPrint) {
        PriorityQueue<Move> bestMoves = new PriorityQueue<>();
        double bestScore = board.getTurn() == Color.WHITE ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
        Move bestGameMove = null;
        CastleState castleState = new CastleState(board.getCastleState());
        Point enPassant = new Point(board.getEnPassant().x, board.getEnPassant().y);
        for (Piece piece : board.getTeam(board.getTurn())) {
            for (Move move : piece.getMoves()) {
                Piece prevEnd = board.move(move);
                double score = scoreGraph(board, 4);
                board.undo(prevEnd, move, castleState, enPassant);
                if (shouldPrint) System.out.println("info currmove " + move + " score cp " + score);
                if ((board.getTurn() == Color.WHITE && score > bestScore)
                        || (board.getTurn() == Color.BLACK && score < bestScore)) {
                    bestScore = score;
                    bestGameMove = move;
                }
            }
        }
        if (shouldPrint) System.out.println("info pv " + bestGameMove);
        return bestGameMove;
    }

    public double scoreGraph(Board root, int depth) {
        if (depth == 0) {
            return root.getWScore() - root.getBScore();
        } else {
            CastleState castleState = new CastleState(board.getCastleState());
            Point enPassant = new Point(board.getEnPassant().x, board.getEnPassant().y);
            double bestScore = board.getTurn() == Color.WHITE ? Double.NEGATIVE_INFINITY : Double.POSITIVE_INFINITY;
            for (Piece piece : board.getTeam(board.getTurn())) {
                for (Move move : piece.getMoves()) {
                    Piece prevEnd = board.move(move);
                    double score = scoreGraph(board, depth - 1);
                    board.undo(prevEnd, move, castleState, enPassant);
                    if ((board.getTurn() == Color.WHITE && score > bestScore)
                            || (board.getTurn() == Color.BLACK && score < bestScore)) {
                        bestScore = score;
                    }
                }
            }
            return bestScore;
        }
    }

}
