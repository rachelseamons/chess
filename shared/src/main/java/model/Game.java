package model;

import chess.ChessGame;

import java.util.Objects;

public class Game {
    private String black;
    private String white;
    private String gameName;
    private ChessGame game = new ChessGame();

    public Game(String gameName) {
        this.gameName = gameName;
    }

    public String getBlack() {
        return black;
    }

    public void setBlack(String black) {
        this.black = black;
    }

    public String getWhite() {
        return white;
    }

    public void setWhite(String white) {
        this.white = white;
    }

    public String getGameName() {
        return gameName;
    }

    public void setGameName(String gameName) {
        this.gameName = gameName;
    }

    @Override
    public boolean equals(Object object) {
        if (this == object) return true;
        if (!(object instanceof Game game1)) return false;
        return Objects.equals(black, game1.black) && Objects.equals(white, game1.white) && Objects.equals(gameName, game1.gameName) && Objects.equals(game, game1.game);
    }

    @Override
    public int hashCode() {
        return Objects.hash(black, white, gameName, game);
    }
}
