package com.eia.felinegraphchronicles.algorithms.mission3;


import com.eia.felinegraphchronicles.modelo.Edge;
import com.eia.felinegraphchronicles.modelo.Graph;
import com.eia.felinegraphchronicles.util.Sentinels;

import java.util.Arrays;

/**
 * Resuelve la Misión 3 con Floyd-Warshall, pero MAXIMIZANDO en vez de
 * minimizando (buscamos el mayor churun posible, no el camino más corto).
 *
 * Complejidad: O(N^3) en tiempo, O(N^2) en espacio.
 * Es la elección correcta porque el enunciado pide la respuesta ENTRE
 * TODOS LOS PARES de nodos, mostrando la matriz N x N completa.
 */
public final class FloydWarshallSolver {

    private FloydWarshallSolver() {
    }

    /**
     * Devuelve la matriz final. Cada celda puede contener:
     * - Sentinels.NO_ROUTE  → no existe ruta (se muestra como "-")
     * - Long.MAX_VALUE      → churun sin límite (se muestra como "inf")
     * - cualquier otro valor → el máximo churun real entre esos dos nodos
     */
    public static long[][] computeMaxMatrix(Graph graph) {
        int n = graph.nodeCount();
        long[][] dist = new long[n][n];

        // Paso 1: inicializamos todo como "no hay ruta"
        for (long[] row : dist) {
            Arrays.fill(row, Sentinels.NO_ROUTE);
        }
        for (int i = 0; i < n; i++) {
            dist[i][i] = 0; // de un nodo a sí mismo, sin moverse, el churun es 0
        }

        // Paso 2: cargamos las aristas directas. Si el mismo par (i,j)
        // aparece repetido, nos quedamos con el peso MÁXIMO (lo pide el enunciado)
        for (Edge edge : graph.edges()) {
            int i = edge.from();
            int j = edge.to();
            long w = edge.weight();
            if (dist[i][j] == Sentinels.NO_ROUTE || w > dist[i][j]) {
                dist[i][j] = w;
            }
        }

        // Paso 3: el triple bucle clásico de Floyd-Warshall, pero con MAX
        // en vez de MIN. k = nodo intermedio que "probamos" usar
        for (int k = 0; k < n; k++) {
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == Sentinels.NO_ROUTE) continue; // no sirve de intermedio si no llego a k
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Sentinels.NO_ROUTE) continue;
                    long candidate = dist[i][k] + dist[k][j];
                    if (dist[i][j] == Sentinels.NO_ROUTE || candidate > dist[i][j]) {
                        dist[i][j] = candidate;
                    }
                }
            }
        }

        // Paso 4: detectamos los pares "sin límite". Regla EXACTA del
        // enunciado: (i,j) es unbounded si existe un k tal que d[i][k] es
        // finito, d[k][k] > 0 (k está sobre un ciclo de ganancia positiva)
        // y d[k][j] es finito.
        boolean[][] unbounded = new boolean[n][n];
        for (int k = 0; k < n; k++) {
            if (dist[k][k] <= 0) continue; // k no forma parte de un ciclo positivo
            for (int i = 0; i < n; i++) {
                if (dist[i][k] == Sentinels.NO_ROUTE) continue;
                for (int j = 0; j < n; j++) {
                    if (dist[k][j] == Sentinels.NO_ROUTE) continue;
                    unbounded[i][j] = true;
                }
            }
        }

        // Paso 5: aplicamos la marca a la matriz final
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (unbounded[i][j]) {
                    dist[i][j] = Long.MAX_VALUE; // representa "inf"
                }
            }
        }

        return dist;
    }
}
