package com.eia.felinegraphchronicles.modelo.resultado;

import java.util.List;

/**
 * Resultado de BFS o DFS sobre el Grid: cuántos movimientos tomó el
 * camino encontrado, y la secuencia de celdas (para poder dibujarla).
 */
public final class PathResult { //para mision 1

    private final boolean reachable;
    private final int moves;
    private final List<int[]> path; // cada elemento es {fila, columna}

    private PathResult(boolean reachable, int moves, List<int[]> path) {
        this.reachable = reachable;
        this.moves = moves;
        this.path = path;
    }

    public static PathResult of(int moves, List<int[]> path) {
        return new PathResult(true, moves, path);
    }

    public static PathResult unreachable() {
        return new PathResult(false, -1, List.of());
    }

    public boolean isReachable() { return reachable; }
    public int moves() { return moves; }
    public List<int[]> path() { return path; }
}
