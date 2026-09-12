package com.eia.felinegraphchronicles.algorithms.mission1;

import com.eia.felinegraphchronicles.modelo.Grid;
import com.eia.felinegraphchronicles.modelo.resultado.PathResult;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Deque;
import java.util.List;

/**
 * Resuelve la Misión 1 con DFS (Depth-First Search), expandiendo los
 * vecinos en el orden fijo: arriba, abajo, izquierda, derecha.
 *
 * Implementado con una PILA EXPLÍCITA (no recursión), porque el grid
 * puede llegar a 1000x1000 = 10^6 celdas, y una DFS recursiva real
 * desbordaría el stack de Java (StackOverflowError).
 *
 * Complejidad: O(R*C) en tiempo y espacio.
 *
 * DFS normalmente NO da el camino más corto, solo UN camino válido —
 * por eso el número esperado es distinto (y usualmente mayor) al de BFS.
 */
public final class DfsSolver {

    private static final int[] D_ROW = {-1, 1, 0, 0};
    private static final int[] D_COL = {0, 0, -1, 1};

    private DfsSolver() {
    }

    public static PathResult solve(Grid grid, int startRow, int startCol, int destRow, int destCol) {
        if (grid.isBomb(startRow, startCol) || grid.isBomb(destRow, destCol)) {
            return PathResult.unreachable();
        }
        if (startRow == destRow && startCol == destCol) {
            return PathResult.of(0, List.of(new int[]{startRow, startCol}));
        }

        int rows = grid.rows();
        int cols = grid.cols();

        boolean[][] visited = new boolean[rows][cols];
        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];
        for (int[] r : parentRow) Arrays.fill(r, -1);
        for (int[] r : parentCol) Arrays.fill(r, -1);

        // CLAVE: cada elemento de la pila es un "frame" = {fila, columna,
        // próxima dirección a probar}. Esto simula exactamente una llamada
        // recursiva: cuando "retrocedemos" (backtrack), recordamos en qué
        // dirección íbamos, igual que la recursión recuerda dónde se quedó.
        Deque<int[]> stack = new ArrayDeque<>();
        visited[startRow][startCol] = true;
        stack.push(new int[]{startRow, startCol, 0});

        boolean found = false;

        while (!stack.isEmpty() && !found) {
            int[] frame = stack.peek(); // miramos el tope SIN sacarlo todavía
            int row = frame[0];
            int col = frame[1];

            if (row == destRow && col == destCol) {
                found = true;
                break;
            }

            if (frame[2] >= 4) {
                // Ya probamos las 4 direcciones desde esta celda: no hay
                // más nada que hacer aquí, retrocedemos (como un "return"
                // en la recursión)
                stack.pop();
                continue;
            }

            int dir = frame[2];
            frame[2]++; // la próxima vez que volvamos a ver este frame, probamos la siguiente dirección

            int nr = row + D_ROW[dir];
            int nc = col + D_COL[dir];

            if (grid.inBounds(nr, nc) && !visited[nr][nc] && !grid.isBomb(nr, nc)) {
                // IMPORTANTE: marcamos "visitado" justo AHORA, al momento de
                // entrar a la celda — no antes, no para todos los vecinos de
                // una vez. Esto es lo que hace que el resultado coincida
                // exactamente con lo que daría una DFS recursiva real
                // (si marcáramos visitado antes de tiempo, el camino
                // encontrado podría ser distinto, porque el grid tiene
                // ciclos: se puede volver a un área por varios caminos).
                visited[nr][nc] = true;
                parentRow[nr][nc] = row;
                parentCol[nr][nc] = col;
                stack.push(new int[]{nr, nc, 0});
            }
        }

        if (!found) {
            return PathResult.unreachable();
        }

        List<int[]> path = reconstructPath(parentRow, parentCol, startRow, startCol, destRow, destCol);
        return PathResult.of(path.size() - 1, path);
    }

    private static List<int[]> reconstructPath(int[][] parentRow, int[][] parentCol,
                                               int startRow, int startCol, int destRow, int destCol) {
        List<int[]> path = new ArrayList<>();
        int row = destRow;
        int col = destCol;
        while (!(row == startRow && col == startCol)) {
            path.add(new int[]{row, col});
            int pr = parentRow[row][col];
            int pc = parentCol[row][col];
            row = pr;
            col = pc;
        }
        path.add(new int[]{startRow, startCol});
        Collections.reverse(path);
        return path;
    }
}
