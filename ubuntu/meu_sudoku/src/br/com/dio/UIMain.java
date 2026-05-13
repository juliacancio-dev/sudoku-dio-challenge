package br.com.dio;

import br.com.dio.ui.custom.screen.MainScreen;
import br.com.dio.util.SudokuGenerator;

import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class UIMain {

    public static void main(String[] args) {
        Map<String, String> gameConfig;
        if (args.length == 0) {
            gameConfig = generateRandomPuzzle(35);
        } else {
            gameConfig = Stream.of(args)
                    .collect(Collectors.toMap(k -> k.split(";")[0], v -> v.split(";")[1]));
        }
        var mainScreen = new MainScreen(gameConfig);
        mainScreen.buildMainScreen();
    }

    private static Map<String, String> generateRandomPuzzle(int filledCells) {
        SudokuGenerator generator = new SudokuGenerator();
        int[][] puzzle = generator.createPuzzle(filledCells);
        Map<String, String> config = new java.util.HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (puzzle[i][j] != 0) {
                    config.put(String.format("%d,%d", i, j), String.format("%d,true", puzzle[i][j]));
                }
            }
        }
        return config;
    }
}
