package com.adamschueller.chesswizard.engine;

import com.adamschueller.chesswizard.engine.move.Castle;
import com.adamschueller.chesswizard.engine.move.Castle.*;
import com.adamschueller.chesswizard.engine.move.EnPassant;
import com.adamschueller.chesswizard.engine.move.Move;
import com.adamschueller.chesswizard.engine.move.Promotion;
import com.adamschueller.chesswizard.engine.move.Promotion.Type;
import com.adamschueller.chesswizard.engine.pieces.Bishop;
import com.adamschueller.chesswizard.engine.pieces.Color;
import com.adamschueller.chesswizard.engine.pieces.King;
import com.adamschueller.chesswizard.engine.pieces.Knight;
import com.adamschueller.chesswizard.engine.pieces.Pawn;
import com.adamschueller.chesswizard.engine.pieces.Piece;
import com.adamschueller.chesswizard.engine.pieces.Queen;
import com.adamschueller.chesswizard.engine.pieces.Rook;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.awt.*;
import java.util.ArrayList;

import static com.adamschueller.chesswizard.engine.move.Castle.*;
import static com.adamschueller.chesswizard.engine.move.Castle.Side.W_KINGSIDE;

@ToString
@AllArgsConstructor
public class Board {
    @Getter @Setter private Piece[][] state = new Piece[8][8];
    @Getter @Setter private int wScore;
    @Getter @Setter private int bScore;
    @Getter private Color turn;
    @Getter @Setter private CastleState castleState;
    @Getter @Setter private Point enPassant;
    @Getter private boolean inCheck;
    @Getter private boolean gameOver;
    @Getter private int $halfMoves;
    @Getter private int $fullMoves;

    public Board() {
        for (int row = 0; row < 8; row++) {
            boolean isFirstRow = false;
            boolean isSecondRow = false;
            Color color = row >= 6 ? Color.BLACK : Color.WHITE;
            if (row == 0 || row == 7) isFirstRow = true;
            if (row == 1 || row == 6) isSecondRow = true;
            for (int col = 0; col < 8; col++) {
                Piece piece;
                if (isFirstRow && (col == 0 || col == 7)) piece = new Rook(color, new Point(col, row), this);
                else if (isFirstRow && (col == 1 || col == 6)) piece = new Knight(color, new Point(col, row), this);
                else if (isFirstRow && (col == 2 || col == 5)) piece = new Bishop(color, new Point(col, row), this);
                else if (isFirstRow && col == 3) piece = new Queen(color, new Point(col, row), this);
                else if (isFirstRow && col == 4) piece = new King(color, new Point(col, row), this);
                else if (isSecondRow) piece = new Pawn(color, new Point(col, row), this);
                else piece = new Piece();

                state[row][col] = piece;
                if (color == Color.WHITE) wScore += piece.score;
                if (color == Color.BLACK) bScore += piece.score;
            }
        }
        turn = Color.WHITE;
        castleState = new CastleState();
        enPassant = new Point(-1, -1);
        $halfMoves = 0;
        $fullMoves = 1;
    }

    public Board(FEN fenState) {
        String[] fenRows = fenState.state.split("/");
        for (int row = 0; row < 8; row++) {
            String fenRow = fenRows[7 - row];
            int col = 0;
            for (int i = 0; i < fenRow.length(); i++) {
                char piece = fenRow.charAt(i);
                if (Character.isDigit(piece)) {
                    int emptySpaces = piece - '0';
                    for (int j = 0; j < emptySpaces; j++) {
                        state[row][col] = new Piece();
                        col++;
                    }
                } else {
                    Color color = Character.isUpperCase(piece) ? Color.WHITE : Color.BLACK;
                    piece = Character.toLowerCase(piece);
                    if (piece == 'r') state[row][col] = new Rook(color, new Point(col, row), this);
                    else if (piece == 'n') state[row][col] = new Knight(color, new Point(col, row), this);
                    else if (piece == 'b') state[row][col] = new Bishop(color, new Point(col, row), this);
                    else if (piece == 'q') state[row][col] = new Queen(color, new Point(col, row), this);
                    else if (piece == 'k') state[row][col] = new King(color, new Point(col, row), this);
                    else if (piece == 'p') state[row][col] = new Pawn(color, new Point(col, row), this);

                    if (color == Color.WHITE) wScore += state[row][col].score;
                    if (color == Color.BLACK) bScore += state[row][col].score;

                    col++;
                }
            }
        }
        turn = fenState.active;
        castleState = fenState.castleState;
        enPassant = fenState.enPassant;
        $halfMoves = fenState.halfmove;
        $fullMoves = fenState.fullmove;
    }

    public Piece move(Move move) {
        Piece piece = state[move.startPos.y][move.startPos.x].clone();
        Piece prevEnd = state[move.endPos.y][move.endPos.x].clone();

        if (move instanceof Castle) {
            castle((Castle) move);
        } else if (move instanceof EnPassant) {
            prevEnd = captureEnPassant(piece, move);
        } else {
            piece.pos = move.endPos;
            prevEnd.pos = move.startPos;

            state[move.endPos.y][move.endPos.x] = piece;
            state[move.startPos.y][move.startPos.x] = new Piece();
        }

        if (move instanceof Promotion) promote(piece, (Promotion) move);
        updateCastleState(piece);
        updateEnPassant(piece, move);

        if (prevEnd.color == Color.WHITE) wScore -= prevEnd.score;
        else bScore -= prevEnd.score;

        turn = turn == Color.WHITE ? Color.BLACK : Color.WHITE;

        return prevEnd;
    }

    public void undo(Piece prevEnd, Move move, CastleState castleState, Point enPassant) {
        Piece piece = state[move.endPos.y][move.endPos.x].clone();

        if (move instanceof Castle) {
            undoCastling((Castle) move);
        } else if (move instanceof EnPassant) {
            undoEnPassant(piece, prevEnd, move);
        } else {
            piece.pos = move.startPos;
            prevEnd.pos = move.endPos;

            state[move.startPos.y][move.startPos.x] = piece;
            state[move.endPos.y][move.endPos.x] = prevEnd;

            if (move instanceof Promotion) undoPromotion(piece, (Promotion) move);
        }

        if (prevEnd.color == Color.WHITE) wScore += prevEnd.score;
        else bScore += prevEnd.score;

        turn = turn == Color.WHITE ? Color.BLACK : Color.WHITE;

        this.castleState = new CastleState(castleState);
        this.enPassant = new Point(enPassant.x, enPassant.y);
    }

    public ArrayList<Piece> getTeam(Color color) {
        ArrayList<Piece> team = new ArrayList<>();
        for (int x = 0; x <= 7; x++) {
            for (int y = 0; y <= 7; y++) {
                if (state[y][x].color == color) team.add(state[y][x]);
            }
        }
        return team;
    }

    public boolean isInCheck(Color color) {
        for (Piece piece : getTeam(color)) {
            if (piece instanceof King) {
                King king = (King) piece;
                return king.inCheck();
            }
        }
        return false;
    }

    public void updateGameOver() {
        boolean isGameOver = true;
        inCheck = isInCheck(turn);
        Color colorCopy = turn == Color.WHITE ? Color.WHITE : Color.BLACK;
        CastleState castleState = new CastleState(getCastleState());
        Point enPassant = new Point(getEnPassant().x, getEnPassant().y);
        for (Piece piece : getTeam(turn)) {
            for (Move move : piece.getMoves()) {
                Piece prevEnd = move(move);
                if (!isInCheck(colorCopy)) isGameOver = false;
                undo(prevEnd, move, castleState, enPassant);
            }
        }
        gameOver = isGameOver;
    }

    public boolean didMoveIntoCheck(Move move) {
        boolean movedIntoCheck = true;
        Color colorCopy = turn == Color.WHITE ? Color.WHITE : Color.BLACK;
        CastleState castleState = new CastleState(getCastleState());
        Point enPassant = new Point(getEnPassant().x, getEnPassant().y);
        Piece prevEnd = move(move);
        if (!isInCheck(colorCopy)) movedIntoCheck = false;
        undo(prevEnd, move, castleState, enPassant);
        return movedIntoCheck;
    }

    public boolean isCastle(String strMove) {
        Move move = new Move(strMove);
        if (turn == Color.WHITE) {
            return move.startPos.equals(W_QUEENSIDE_START.kingPos) && move.endPos.equals(W_QUEENSIDE_END.kingPos) ||
                    move.startPos.equals(W_KINGSIDE_START.kingPos) && move.endPos.equals(W_KINGSIDE_END.kingPos);
        } else {
            return move.startPos.equals(B_QUEENSIDE_START.kingPos) && move.endPos.equals(B_QUEENSIDE_END.kingPos) ||
                    move.startPos.equals(B_KINGSIDE_START.kingPos) && move.endPos.equals(B_KINGSIDE_END.kingPos);
        }
    }

    public boolean canPromote(Move move) {
        if (state[move.startPos.y][move.startPos.x] instanceof Pawn) {
            Pawn pawn = (Pawn) state[move.startPos.y][move.startPos.x];
            if (pawn.color == Color.WHITE && move.endPos.y == 7) return true;
            else if (pawn.color == Color.BLACK && move.endPos.y == 0) return true;
            else return false;
        } else {
            return false;
        }
    }

    public boolean isMoveValid(Move move) {
        return state[move.startPos.y][move.startPos.x].getMoves().contains(move) && !didMoveIntoCheck(move);
    }

    public boolean isEmptyOrAttacking(Piece start, Point pos) {
        if (contains(pos)) {
            Piece end = state[pos.y][pos.x];
            return end.notation == '-' || isAttacking(start, pos);
        } else {
            return false;
        }
    }

    public boolean isAttacking(Piece attacker, Point pos) {
        if (contains(pos)) {
            Piece end = state[pos.y][pos.x];
            return end.notation != '-' && attacker.color != end.color;
        } else {
            return false;
        }
    }

    public boolean contains(Point pos) {
        return (pos.x >= 0 && pos.x <= 7) && (pos.y >= 0 && pos.y <= 7);
    }

    private void updateCastleState(Piece piece) {
        char notation = Character.toLowerCase(piece.notation);
        if (notation == 'r') {
            if (piece.pos.x < 4) {
                if (piece.color == Color.WHITE) castleState.wQueenside = false;
                else castleState.bQueenside = false;
            } else {
                if (piece.color == Color.WHITE) castleState.wKingside = false;
                else castleState.bKingside = false;
            }
        }
        if (notation == 'k') {
            if (piece.color == Color.WHITE) castleState.wCastle();
            else castleState.bCastle();
        }
    }

    private void castle(Castle castle) {
        Position startPos = castle.getStartPos();
        Position endPos = castle.getEndPos();
        Color color = castle.side == Side.W_QUEENSIDE || castle.side == W_KINGSIDE ? Color.WHITE : Color.BLACK;
        int y = endPos.kingPos.y;

        state[y][startPos.rookPos.x] = new Piece();
        if (castle.side == Side.W_QUEENSIDE || castle.side == Side.B_QUEENSIDE)
            state[y][endPos.kingPos.x-1] = new Piece();
        state[y][endPos.kingPos.x] = new King(color, endPos.kingPos, this);
        state[y][endPos.rookPos.x] = new Rook(color, endPos.rookPos, this);
        state[y][startPos.kingPos.x] = new Piece();

        if (color == Color.WHITE) castleState.wCastle();
        else castleState.bCastle();
    }

    private void undoCastling(Castle castle) {
        Position startPos = castle.getStartPos();
        Position endPos = castle.getEndPos();
        Color color = castle.side == Side.W_QUEENSIDE || castle.side == W_KINGSIDE ? Color.WHITE : Color.BLACK;
        int y = endPos.kingPos.y;

        state[y][startPos.rookPos.x] = new Rook(color, endPos.rookPos, this);
        state[y][endPos.kingPos.x] = new Piece();
        state[y][endPos.rookPos.x] = new Piece();
        state[y][startPos.kingPos.x] = new King(color, endPos.kingPos, this);
    }

    private void updateEnPassant(Piece piece, Move move) {
        boolean isEnPassant = piece.color == Color.WHITE ? move.endPos.y - move.startPos.y == 2
                : move.startPos.y - move.endPos.y == 2;
        if (piece instanceof Pawn && isEnPassant) {
            if (piece.color == Color.WHITE) enPassant = new Point(move.endPos.x, move.endPos.y - 1);
            else enPassant = new Point(move.endPos.x, move.endPos.y + 1);
        }
        else enPassant = new Point(-1, -1);
    }

    private Piece captureEnPassant(Piece piece, Move move) {
        Point targetPos;
        if (piece.color == Color.WHITE) targetPos = new Point(move.endPos.x, move.endPos.y - 1);
        else targetPos = new Point(move.endPos.x, move.endPos.y + 1);
        Piece prevEnd = state[targetPos.y][targetPos.x].clone();

        piece.pos = move.endPos;
        prevEnd.pos = targetPos;

        state[move.endPos.y][move.endPos.x] = piece;
        state[move.startPos.y][move.startPos.x] = new Piece();
        state[targetPos.y][targetPos.x] = new Piece();

        return prevEnd;
    }

    private void undoEnPassant(Piece piece, Piece prevEnd, Move move) {
        Point targetPos;
        if (piece.color == Color.WHITE) targetPos = new Point(move.endPos.x, move.endPos.y - 1);
        else targetPos = new Point(move.endPos.x, move.endPos.y + 1);

        piece.pos = move.startPos;
        prevEnd.pos = targetPos;

        state[move.startPos.y][move.startPos.x] = piece;
        state[move.endPos.y][move.endPos.x] = new Piece();
        state[targetPos.y][targetPos.x] = prevEnd;
    }

    private void promote(Piece piece, Promotion promotion) {
        if (promotion.type == Type.KNIGHT) {
            state[piece.pos.y][piece.pos.x] = new Knight(piece.color, piece.pos, this);
            if (piece.color == Color.WHITE) wScore += (Knight.SCORE - Pawn.SCORE);
            else bScore += (Knight.SCORE - Pawn.SCORE);
        }
        else {
            state[piece.pos.y][piece.pos.x] = new Queen(piece.color, piece.pos, this);
            if (piece.color == Color.WHITE) wScore += (Queen.SCORE - Pawn.SCORE);
            else bScore += (Queen.SCORE - Pawn.SCORE);
        }
    }

    private void undoPromotion(Piece piece, Promotion promotion) {
        state[piece.pos.y][piece.pos.x] = new Pawn(piece.color, piece.pos, this);
        if (promotion.type == Type.KNIGHT) {
            if (piece.color == Color.WHITE) wScore -= (Knight.SCORE - Pawn.SCORE);
            else bScore -= (Knight.SCORE - Pawn.SCORE);
        }
        else {
            if (piece.color == Color.WHITE) wScore -= (Queen.SCORE - Pawn.SCORE);
            else bScore -= (Queen.SCORE - Pawn.SCORE);
        }
    }

    public static class FEN {
        String state;
        Color active;
        CastleState castleState;
        Point enPassant;
        int halfmove;
        int fullmove;

        public FEN(String[] cmd) {
            this.state = cmd[2];
            this.active = cmd[3].equals("w") ? Color.WHITE : Color.BLACK;
            this.castleState = new CastleState(cmd[4]);
            this.enPassant = !cmd[5].equals("-")
                    ? new Point(cmd[5].charAt(0) - 'a', cmd[5].charAt(1) - '0' - 1)
                    : new Point(-1, -1);
            this.halfmove = Integer.parseInt(cmd[6]);
            this.fullmove = Integer.parseInt(cmd[7]);
        }
    }

    @ToString
    @NoArgsConstructor
    public static class CastleState {
        public boolean wQueenside = true;
        public boolean wKingside = true;
        public boolean bQueenside = true;
        public boolean bKingside = true;

        public CastleState(CastleState copy) {
            this.wQueenside = copy.wQueenside;
            this.wKingside = copy.wKingside;
            this.bQueenside = copy.bQueenside;
            this.bKingside = copy.bKingside;
        }

        public CastleState(String castlingString) {
            wQueenside = false;
            wKingside = false;
            bQueenside = false;
            bKingside = false;
            for (int i = 0; i < castlingString.length(); i++) {
                char castlingChar = castlingString.charAt(i);
                if (castlingChar == 'K') wKingside = true;
                if (castlingChar == 'Q') wQueenside = true;
                if (castlingChar == 'k') bKingside = true;
                if (castlingChar == 'q') bQueenside = true;
            }
        }

        public void wCastle() {
            wQueenside = false;
            wKingside = false;
        }

        public void bCastle() {
            bQueenside = false;
            bKingside = false;
        }
    }
}
