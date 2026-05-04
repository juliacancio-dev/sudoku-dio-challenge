package br.com.dio.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class SudokuGenerator {
    private static final int SIZE = 9;
    private static final int SUBGRID = 3;
    private Random rand = new Random();

    public int[][] generateFull() {
        int[][] b = new int[9][9];
        fillBoard(b);
        return b;
    }

    private boolean fillBoard(int[][] b) {
        for (int r = 0; r < 9; r++) {
            for (int c = 0; c < 9; c++) {
                if (b[r][c] == 0) {
                    List<Integer> nums = getShuffled();
                    for (int n : nums) {
                        if (valid(b, r, c, n)) {
                            b[r][c] = n;
                            if (fillBoard(b)) {
                                return true;
                            }
                            b[r][c] = 0;
                        }
                    }
                    return false;
                }
            }
        }
        return true;
    }

    private List<Integer> getShuffled() {
        List<Integer> nums = new ArrayList();
        for (int i = 1; i <= 9; i++) {
            nums.add(i);
        }
        Collections.shuffle(nums, this.rand);
        return nums;
    }

    private boolean valid(int[][] b, int row, int col, int num) {
        // Check row
        for (int i = 0; i < 9; i++) {
            if (b[row][i] == num) {
                return false;
            }
        }
        // Check column
        for (int i = 0; i < 9; i++) {
            if (b[i][col] == num) {
                return false;
            }
        }
        // Check 3x3 block
        int br = row - row % 3;
        int bc = col - col % 3;
        for (int r = br; r < br + 3; r++) {
            for (int c = bc; c < bc + 3; c++) {
                if (b[r][c] == num) {
                    return false;
                }
            }
        }
        return true;
    }

    public int[][] createPuzzle(int filled) {
        int[][] sol = generateFull();
        int[][] puz = new int[9][9];
        for (int i = 0; i < 9; i++) {
            puz[i] = sol[i].clone();
        }
        List<Integer> pos = new ArrayList();
        for (int i = 0; i < 81; i++) {
            pos.add(i);
        }
        Collections.shuffle(pos, this.rand);
        int toRemove = 81 - filled;
        int removed = 0;
        for (int idx = 0; idx < pos.size() && removed < toRemove; idx++) {
            int p = pos.get(idx);
            int r = p / 9;
            int c = p % 9;
            int backup = puz[r][c];
            puz[r][c] = 0;
            // Verificar se o puzzle ainda tem solução única
            int[][] copy = new int[9][9];
            for (int i = 0; i < 9; i++) {
                copy[i] = puz[i].clone();
            }
            int solutions = countSolutions(copy);
            if (solutions != 1) {
                puz[r][c] = backup; // reverter se não for única
            } else {
                removed++;
            }
        }
        return puz;
    }

    // Conta o número de soluções (máximo 2 para performance)
    private int countSolutions(int[][] board) {
        int[] count = new int[1];
        count[0] = 0;
        solveCount(board, count);
        return count[0];
    }

    private void solveCount(int[][] board, int[] count) {
        if (count[0] >= 2) return; // Parar se já encontrou 2 soluções
        for (int r = 0; r < SIZE; r++) {
            for (int c = 0; c < SIZE; c++) {
                if (board[r][c] == 0) {
                    for (int n = 1; n <= SIZE; n++) {
                        if (valid(board, r, c, n)) {
                            board[r][c] = n;
                            solveCount(board, count);
                            board[r][c] = 0;
                        }
                    }
                    return;
                }
            }
        }
        // Tabuleiro completo - encontrou uma solução
        count[0]++;
    }
}
