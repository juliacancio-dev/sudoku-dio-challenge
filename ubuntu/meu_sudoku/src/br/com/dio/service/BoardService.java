package br.com.dio.service;

import br.com.dio.model.Board;
import br.com.dio.model.GameStatusEnum;
import br.com.dio.model.Space;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class BoardService {

    private final static int BOARD_LIMIT = 9;
    private static final int MAX_LIVES = 3;

    private final Board board;
    private int lives;

    public BoardService(final Map<String, String> gameConfig) {
        this.board = new Board(initBoard(gameConfig));
        this.lives = MAX_LIVES;
    }

    public List<List<Space>> getSpaces(){
        return board.getSpaces();
    }

    public Board getBoard() {
        return board;
    }

    public void reset(){
        board.reset();
        this.lives = MAX_LIVES;
    }

    public boolean hasErrors(){
        return board.hasErrors();
    }

    public GameStatusEnum getStatus(){
        return board.getStatus();
    }

    public boolean gameIsFinished(){
        return board.gameIsFinished();
    }

    public boolean setCellValue(int row, int col, int value) {
        return board.changeValue(col, row, value);
    }

    public boolean clearCell(int row, int col) {
        return board.clearValue(col, row);
    }

    public int getLives() {
        return lives;
    }

    public void loseLife() {
        if (lives > 0) {
            lives--;
        }
    }

    public boolean hasLives() {
        return lives > 0;
    }

    private List<List<Space>> initBoard(final Map<String, String> gameConfig) {
        List<List<Space>> spaces = new ArrayList<>();
        for (int col = 0; col < BOARD_LIMIT; col++) {
            spaces.add(new ArrayList<>());
            for (int row = 0; row < BOARD_LIMIT; row++) {
                var positionConfig = gameConfig.get("%s,%s".formatted(row, col));
                if (positionConfig == null || positionConfig.trim().isEmpty()) {
                    positionConfig = "0,false";
                }
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed, row, col);
                spaces.get(col).add(currentSpace);
            }
        }

        return spaces;
    }
}
