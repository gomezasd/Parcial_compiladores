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
 * Resuelve la Misión 1 con BFS (Breadth-First Search).
 *
 * Complejidad: O(R*C) en tiempo y espacio, porque cada celda se visita
 * como máximo una vez y tiene a lo sumo 4 vecinos.
 *
 * BFS es la elección correcta aquí porque en un grafo NO PONDERADO (cada
 * movimiento cuesta 1 paso), BFS SIEMPRE encuentra el camino con el menor
 * número de pasos posible — algo que DFS no garantiza.
 */
public final class BfsSolver {

    // Orden fijo de direcciones: arriba, abajo, izquierda, derecha
    private static final int[] D_ROW = {-1, 1, 0, 0};
    private static final int[] D_COL = {0, 0, -1, 1};

    private BfsSolver() {
    }

    public static PathResult solve(Grid grid, int startRow, int startCol, int destRow, int destCol) {
        // Regla del enunciado: si inicio o destino tienen bomba, es inalcanzable
        if (grid.isBomb(startRow, startCol) || grid.isBomb(destRow, destCol)) {
            return PathResult.unreachable();
        }
        // Regla del enunciado: si inicio == destino, la respuesta es 0
        if (startRow == destRow && startCol == destCol) {
            return PathResult.of(0, List.of(new int[]{startRow, startCol}));
        }

        int rows = grid.rows();
        int cols = grid.cols();

        // visited: qué celdas ya exploramos. parentRow/parentCol: desde
        // qué celda llegamos a cada una, para reconstruir el camino al final.
        boolean[][] visited = new boolean[rows][cols];
        int[][] parentRow = new int[rows][cols];
        int[][] parentCol = new int[rows][cols];
        for (int[] r : parentRow) Arrays.fill(r, -1);
        for (int[] r : parentCol) Arrays.fill(r, -1);

        // BFS usa una COLA (FIFO): el primero en entrar es el primero en salir
        Deque<int[]> queue = new ArrayDeque<>();
        queue.add(new int[]{startRow, startCol});
        visited[startRow][startCol] = true;

        boolean found = false;

        while (!queue.isEmpty() && !found) {
            int[] current = queue.poll(); // sacamos el más antiguo de la cola
            int row = current[0];
            int col = current[1];

            if (row == destRow && col == destCol) {
                found = true;
                break;
            }

            // probamos los 4 vecinos en orden fijo
            for (int dir = 0; dir < 4; dir++) {
                int nr = row + D_ROW[dir];
                int nc = col + D_COL[dir];
                if (grid.inBounds(nr, nc) && !visited[nr][nc] && !grid.isBomb(nr, nc)) {
                    visited[nr][nc] = true;
                    parentRow[nr][nc] = row;
                    parentCol[nr][nc] = col;
                    queue.add(new int[]{nr, nc});
                }
            }
        }

        if (!found) {
            return PathResult.unreachable();
        }

        List<int[]> path = reconstructPath(parentRow, parentCol, startRow, startCol, destRow, destCol);
        return PathResult.of(path.size() - 1, path); // -1 porque el camino incluye la celda de inicio
    }

    // Reconstruye el camino recorriendo los "padres" desde el destino hasta
    // el inicio, y luego invierte la lista para que quede en orden correcto
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
