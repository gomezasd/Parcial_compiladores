package com.eia.felinegraphchronicles.algorithms.mission4;

import java.util.*;

/**
 * Resuelve la Misión 4 con Kruskal (Árbol de Expansión Mínima), apoyado
 * en Union-Find con compresión de caminos y unión por tamaño.
 *
 * Complejidad: O(C log C), dominada por ordenar los C cables.
 * Es la elección correcta porque buscamos conectar TODOS los nodos al
 * menor costo total, sin que importe un nodo "raíz" — a diferencia de
 * Prim, que sí necesita empezar desde un nodo concreto.
 */
public final class Kruskal {

    private Kruskal() {
    }

    public record Edge(int u, int v, long cost) {
    }

    public record Result(long totalCost, boolean connected, List<Edge> usedEdges) {
    }

    static final class UnionFind {
        private final int[] parent, size;

        UnionFind(int n) {
            parent = new int[n];
            size = new int[n];
            for (int i = 0; i < n; i++) { parent[i] = i; size[i] = 1; }
        }

        int find(int x) {
            if (parent[x] != x) parent[x] = find(parent[x]); // compresión de caminos
            return parent[x];
        }

        boolean union(int a, int b) {
            int ra = find(a), rb = find(b);
            if (ra == rb) return false; // ya conectados (o self-loop)
            if (size[ra] < size[rb]) { int tmp = ra; ra = rb; rb = tmp; } // unión por tamaño
            parent[rb] = ra;
            size[ra] += size[rb];
            return true;
        }
    }

    /** @param n intersecciones numeradas de 1 a n. */
    public static Result solve(int n, List<Edge> edges) {
        List<Edge> sorted = new ArrayList<>(edges);
        sorted.sort(Comparator.comparingLong(Edge::cost));

        UnionFind uf = new UnionFind(n + 1);
        List<Edge> used = new ArrayList<>();
        long total = 0;

        for (Edge e : sorted) {
            if (uf.union(e.u(), e.v())) {
                total += e.cost();
                used.add(e);
                if (used.size() == n - 1) break; // MST completo, no hace falta seguir
            }
        }

        boolean connected = used.size() == n - 1;
        return new Result(connected ? total : -1, connected, connected ? used : List.of());
    }
}
