package br.com.dio;

import br.com.dio.util.SudokuGenerator;
import java.lang.reflect.Method;

public class TestSudoku {

    public static void main(String[] args) throws Exception {
        SudokuGenerator generator = new SudokuGenerator();

        // Generate a puzzle with 35 filled cells
        int[][] puzzle = generator.createPuzzle(35);

        // Print the puzzle as a 9x9 grid
        System.out.println("Generated Sudoku Puzzle (35 filled cells):");
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
        for (int r = 0; r < 9; r++) {
            System.out.print("| ");
            for (int c = 0; c < 9; c++) {
                System.out.print((puzzle[r][c] == 0 ? "." : puzzle[r][c]) + " ");
                if ((c + 1) % 3 == 0) System.out.print("| ");
            }
            System.out.println();
            if ((r + 1) % 3 == 0 && r < 8) {
                System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");
            }
        }
        System.out.println("+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+-+");

        // Use reflection to access private countSolutions method
        Method method = SudokuGenerator.class.getDeclaredMethod("countSolutions", int[][].class);
        method.setAccessible(true);

        // Create a copy of the puzzle to pass to countSolutions
        int[][] copy = new int[9][9];
        for (int i = 0; i < 9; i++) {
            copy[i] = puzzle[i].clone();
        }

        int solutions = (Integer) method.invoke(generator, (Object) copy);

        boolean valid = (solutions == 1);
        System.out.println();
        System.out.println("Number of solutions: " + solutions);
        System.out.println("Puzzle is " + (valid ? "VALID (unique solution)" : "INVALID (not a unique solution)"));
    }
}
