package br.com.dio.util;

import br.com.dio.model.Board;
import br.com.dio.model.Space;

import java.util.List;

public class SudokuValidator {

    private static final int BOARD_SIZE = 9;
    private static final int SUBGRID_SIZE = 3;

    public static boolean isValid(Board board) {
        List<List<Space>> spaces = board.getSpaces();

        // Validar todas as linhas
        for (int row = 0; row < BOARD_SIZE; row++) {
            if (!isValidRow(spaces, row)) {
                return false;
            }
        }

        // Validar todas as colunas
        for (int col = 0; col < BOARD_SIZE; col++) {
            if (!isValidColumn(spaces, col)) {
                return false;
            }
        }

        // Validar todos os blocos 3x3
        for (int blockRow = 0; blockRow < BOARD_SIZE; blockRow += SUBGRID_SIZE) {
            for (int blockCol = 0; blockCol < BOARD_SIZE; blockCol += SUBGRID_SIZE) {
                if (!isValidBlock(spaces, blockRow, blockCol)) {
                    return false;
                }
            }
        }

        return true;
    }

    private static boolean isValidRow(List<List<Space>> spaces, int row) {
        boolean[] seen = new boolean[BOARD_SIZE + 1]; // índices 1-9
        for (int col = 0; col < BOARD_SIZE; col++) {
            Space space = spaces.get(col).get(row);
            Integer value = space.getActual();
            if (value != null && value >= 1 && value <= 9) {
                if (seen[value]) {
                    return false; // Duplicata na linha
                }
                seen[value] = true;
            }
        }
        return true;
    }

    private static boolean isValidColumn(List<List<Space>> spaces, int col) {
        boolean[] seen = new boolean[BOARD_SIZE + 1]; // índices 1-9
        for (int row = 0; row < BOARD_SIZE; row++) {
            Space space = spaces.get(col).get(row);
            Integer value = space.getActual();
            if (value != null && value >= 1 && value <= 9) {
                if (seen[value]) {
                    return false; // Duplicata na coluna
                }
                seen[value] = true;
            }
        }
        return true;
    }

    private static boolean isValidBlock(List<List<Space>> spaces, int startRow, int startCol) {
        boolean[] seen = new boolean[BOARD_SIZE + 1]; // índices 1-9
        for (int r = startRow; r < startRow + SUBGRID_SIZE; r++) {
            for (int c = startCol; c < startCol + SUBGRID_SIZE; c++) {
                Space space = spaces.get(c).get(r);
                Integer value = space.getActual();
                if (value != null && value >= 1 && value <= 9) {
                    if (seen[value]) {
                        return false; // Duplicata no bloco
                    }
                    seen[value] = true;
                }
            }
        }
        return true;
    }

    public static boolean hasDuplicatesInRow(List<List<Space>> spaces, int row) {
        return !isValidRow(spaces, row);
    }

    public static boolean hasDuplicatesInColumn(List<List<Space>> spaces, int col) {
        return !isValidColumn(spaces, col);
    }

    public static boolean hasDuplicatesInBlock(List<List<Space>> spaces, int row, int col) {
        int blockRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int blockCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        return !isValidBlock(spaces, blockRow, blockCol);
    }

    public static boolean hasConflicts(Board board, int row, int col, int value) {
        List<List<Space>> spaces = board.getSpaces();

        // Verificar linha
        for (int c = 0; c < BOARD_SIZE; c++) {
            if (c != col) {
                Space other = spaces.get(c).get(row);
                if (other.getActual() != null && other.getActual() == value) {
                    return true;
                }
            }
        }

        // Verificar coluna
        for (int r = 0; r < BOARD_SIZE; r++) {
            if (r != row) {
                Space other = spaces.get(col).get(r);
                if (other.getActual() != null && other.getActual() == value) {
                    return true;
                }
            }
        }

        // Verificar bloco 3x3
        int blockRow = (row / SUBGRID_SIZE) * SUBGRID_SIZE;
        int blockCol = (col / SUBGRID_SIZE) * SUBGRID_SIZE;
        for (int r = blockRow; r < blockRow + SUBGRID_SIZE; r++) {
            for (int c = blockCol; c < blockCol + SUBGRID_SIZE; c++) {
                if (r != row || c != col) {
                    Space other = spaces.get(c).get(r);
                    if (other.getActual() != null && other.getActual() == value) {
                        return true;
                    }
                }
            }
        }

        return false;
    }
}
