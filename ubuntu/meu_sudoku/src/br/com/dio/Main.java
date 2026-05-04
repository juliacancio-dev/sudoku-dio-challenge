package br.com.dio;

import br.com.dio.model.Board;
import br.com.dio.model.Space;
import br.com.dio.util.SudokuGenerator;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static br.com.dio.util.BoardTemplate.BOARD_TEMPLATE;
import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

public class Main {

    private final static Scanner scanner = new Scanner(System.in);

    private static Board board;

    private final static int BOARD_LIMIT = 9;

    public static void main(String[] args) {
        Map<String, String> positions;
        if (args.length == 0) {
            positions = generateRandomPuzzle(35);
            System.out.println("Novo jogo com puzzle aleatorio iniciado!");
        } else {
            positions = Stream.of(args)
                    .collect(Collectors.toMap(k -> k.split(";")[0], v -> v.split(";")[1]));
        }
        var option = -1;
        while (true){
            System.out.println("Selecione uma das opcoes a seguir");
            System.out.println("1 - Iniciar um novo Jogo");
            System.out.println("2 - Colocar um novo numero");
            System.out.println("3 - Remover um numero");
            System.out.println("4 - Visualizar jogo atual");
            System.out.println("5 - Verificar status do jogo");
            System.out.println("6 - limpar jogo");
            System.out.println("7 - Finalizar jogo");
            System.out.println("8 - Sair");

            option = scanner.nextInt();

            switch (option){
                case 1 -> startGame(positions);
                case 2 -> inputNumber();
                case 3 -> removeNumber();
                case 4 -> showCurrentGame();
                case 5 -> showGameStatus();
                case 6 -> clearGame();
                case 7 -> finishGame();
                case 8 -> System.exit(0);
                default -> System.out.println("Opcao invalida, selecione uma das opcoes do menu");
            }
        }
    }

    private static Map<String, String> generateRandomPuzzle(int filledCells) {
        SudokuGenerator generator = new SudokuGenerator();
        int[][] puzzle = generator.createPuzzle(filledCells);
        Map<String, String> config = new java.util.HashMap<>();
        for (int i = 0; i < 9; i++) {
            for (int j = 0; j < 9; j++) {
                if (puzzle[i][j] != 0) {
                    config.put(String.format("%d,%d", j, i), String.format("%d,true", puzzle[i][j]));
                }
            }
        }
        return config;
    }

    private static void startGame(final Map<String, String> positions) {
        if (nonNull(board)){
            System.out.println("O jogo ja foi iniciado");
            return;
        }

        List<List<Space>> spaces = new ArrayList<>();
        for (int i = 0; i < BOARD_LIMIT; i++) {
            spaces.add(new ArrayList<>());
            for (int j = 0; j < BOARD_LIMIT; j++) {
                var positionConfig = positions.get("%s,%s".formatted(i, j));
                if (positionConfig == null || positionConfig.trim().isEmpty()) {
                    positionConfig = "0,false";
                }
                var expected = Integer.parseInt(positionConfig.split(",")[0]);
                var fixed = Boolean.parseBoolean(positionConfig.split(",")[1]);
                var currentSpace = new Space(expected, fixed, j, i);
                spaces.get(i).add(currentSpace);
            }
        }

        board = new Board(spaces);
        System.out.println("O jogo esta pronto para comecar");
    }


    private static void inputNumber() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        System.out.println("Informe a coluna em que o numero sera inserido");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o numero sera inserido");
        var row = runUntilGetValidNumber(0, 8);
        System.out.printf("Informe o numero que vai entrar na posicao [%s,%s]\n", col, row);
        var value = runUntilGetValidNumber(1, 9);
        if (!board.changeValue(col, row, value)){
            System.out.printf("A posicao [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void removeNumber() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        System.out.println("Informe a coluna em que o numero sera removido");
        var col = runUntilGetValidNumber(0, 8);
        System.out.println("Informe a linha em que o numero sera removido");
        var row = runUntilGetValidNumber(0, 8);
        if (!board.clearValue(col, row)){
            System.out.printf("A posicao [%s,%s] tem um valor fixo\n", col, row);
        }
    }

    private static void showCurrentGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        var args = new Object[81];
        var argPos = 0;
        for (int i = 0; i < BOARD_LIMIT; i++) {
            for (var col: board.getSpaces()){
                args[argPos ++] = " " + ((isNull(col.get(i).getActual())) ? " " : col.get(i).getActual());
            }
        }
        System.out.println("Seu jogo se encontra da seguinte forma");
        System.out.printf((BOARD_TEMPLATE) + "\n", args);
    }

    private static void showGameStatus() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        System.out.printf("O jogo atualmente se encontra no status %s\n", board.getStatus().getLabel());
        if(board.hasErrors()){
            System.out.println("O jogo contem erros");
        } else {
            System.out.println("O jogo nao contem erros");
        }
    }

    private static void clearGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        System.out.println("Tem certeza que deseja limpar seu jogo e perder todo seu progresso?");
        var confirm = scanner.next();
        while (!confirm.equalsIgnoreCase("sim") && !confirm.equalsIgnoreCase("nao")){
            System.out.println("Informe sim ou nao");
            confirm = scanner.next();
        }

        if(confirm.equalsIgnoreCase("sim")){
            board.reset();
        }
    }

    private static void finishGame() {
        if (isNull(board)){
            System.out.println("O jogo ainda nao foi iniciado");
            return;
        }

        if (board.gameIsFinished()){
            System.out.println("Parabens voce concluiu o jogo");
            showCurrentGame();
            board = null;
        } else if (board.hasErrors()) {
            System.out.println("Seu jogo contem erros, verifique seu board e ajuste-o");
        } else {
            System.out.println("Voce ainda precisa preencher algum espaco");
        }
    }


    private static int runUntilGetValidNumber(final int min, final int max){
        var current = scanner.nextInt();
        while (current < min || current > max){
            System.out.printf("Informe um numero entre %s e %s\n", min, max);
            current = scanner.nextInt();
        }
        return current;
    }
}
