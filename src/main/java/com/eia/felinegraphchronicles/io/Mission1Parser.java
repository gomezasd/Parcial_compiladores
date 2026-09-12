package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.modelo.Grid;

import java.util.ArrayList;
import java.util.List;

/**
 * Traduce el texto de input de la Misión 1 (formato descrito en la
 * Sección 3 del enunciado) a una lista de Mission1Case, uno por cada
 * test case, DETENIÉNDOSE quando encuentra R=0 y C=0 (esa línea NO se
 * procesa como caso, según la especificación).
 */
public final class Mission1Parser {

    private Mission1Parser() {
    }

    public static List<Mission1Case> parse(String rawInput) {
        InputTokenizer tokens = new InputTokenizer(rawInput);
        List<Mission1Case> cases = new ArrayList<>();

        int caseNumber = 0;
        while (tokens.hasNextInt()) {
            caseNumber++;

            int rows = tokens.nextInt();
            int cols = tokens.nextInt();

            // Terminador especial del enunciado: R=0, C=0 significa "fin
            // del input", y esa línea NO se procesa como caso
            if (rows == 0 && cols == 0) {
                break;
            }

            if (rows < 0 || cols < 0) {
                throw new ParseException(
                        "Caso #%d: filas y columnas no pueden ser negativas (R=%d, C=%d)"
                                .formatted(caseNumber, rows, cols));
            }

            Grid grid = new Grid(rows, cols);

            // Cantidad de filas que contienen bombas
            int bombRowCount = tokens.nextInt();
            for (int i = 0; i < bombRowCount; i++) {
                int rowNumber = tokens.nextInt();
                int bombsInRow = tokens.nextInt();
                for (int j = 0; j < bombsInRow; j++) {
                    int col = tokens.nextInt();
                    grid.markBomb(rowNumber, col);
                }
            }

            int startRow = tokens.nextInt();
            int startCol = tokens.nextInt();
            int destRow = tokens.nextInt();
            int destCol = tokens.nextInt();

            cases.add(new Mission1Case(grid, startRow, startCol, destRow, destCol));
        }

        return cases;
    }
}
