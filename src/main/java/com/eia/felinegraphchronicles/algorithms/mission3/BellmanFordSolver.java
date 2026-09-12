package com.eia.felinegraphchronicles.algorithms.mission3;

import com.eia.felinegraphchronicles.modelo.Edge;
import com.eia.felinegraphchronicles.modelo.Graph;
import com.eia.felinegraphchronicles.util.Sentinels;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public final class BellmanFordSolver {

    private BellmanFordSolver() {
    }

    public static final class Result {
        public final long[] dist;
        public final boolean[] unbounded;
        private final int[] predecessor;
        private final int[] causingSeed; // -1 si el nodo no es unbounded

        Result(long[] dist, boolean[] unbounded, int[] predecessor, int[] causingSeed) {
            this.dist = dist;
            this.unbounded = unbounded;
            this.predecessor = predecessor;
            this.causingSeed = causingSeed;
        }

        /** Reconstruye la ruta de máximo churun desde source hasta destination (caso OK). */
        public List<Integer> reconstructPath(int source, int destination) {
            List<Integer> path = new ArrayList<>();
            int current = destination;
            while (current != source) {
                path.add(current);
                current = predecessor[current];
                if (current == -1) return List.of(); // no debería pasar si es alcanzable
            }
            path.add(source);
            java.util.Collections.reverse(path);
            return path;
        }

        /**
         * Reconstruye el ciclo de ganancia positiva responsable de que
         * 'destination' sea unbounded. Usa la técnica clásica de Bellman-Ford:
         * caminar 'nodeCount' pasos por los predecesores garantiza caer
         * DENTRO del ciclo (por principio del palomar: un camino de más
         * de nodeCount-1 aristas sin ciclos es imposible).
         */
        public List<Integer> reconstructCycle(int destination, int nodeCount) {
            int seed = causingSeed[destination];
            if (seed == -1) return List.of();

            int current = seed;
            for (int i = 0; i < nodeCount; i++) {
                if (predecessor[current] == -1) return List.of();
                current = predecessor[current];
            }

            List<Integer> cycle = new ArrayList<>();
            int node = current;
            do {
                cycle.add(node);
                node = predecessor[node];
            } while (node != current && cycle.size() <= nodeCount);
            return cycle;
        }
    }

    public static Result solve(Graph graph, int source) {
        int n = graph.nodeCount();
        long[] dist = new long[n];
        int[] predecessor = new int[n];
        Arrays.fill(dist, Sentinels.UNREACHABLE);
        Arrays.fill(predecessor, -1);
        dist[source] = 0;

        List<Edge> edges = graph.edges();

        for (int round = 0; round < n - 1; round++) {
            boolean improved = false;
            for (Edge edge : edges) {
                if (dist[edge.from()] == Sentinels.UNREACHABLE) continue;
                long candidate = dist[edge.from()] + edge.weight();
                if (candidate > dist[edge.to()]) {
                    dist[edge.to()] = candidate;
                    predecessor[edge.to()] = edge.from();
                    improved = true;
                }
            }
            if (!improved) break;
        }

        boolean[] seedUnbounded = new boolean[n];
        for (Edge edge : edges) {
            if (dist[edge.from()] == Sentinels.UNREACHABLE) continue;
            long candidate = dist[edge.from()] + edge.weight();
            if (candidate > dist[edge.to()]) {
                seedUnbounded[edge.to()] = true;
                predecessor[edge.to()] = edge.from(); // puntero válido dentro/cerca del ciclo
            }
        }

        PropagationResult propagation = propagateUnbounded(graph, seedUnbounded);
        return new Result(dist, propagation.unbounded, predecessor, propagation.causingSeed);
    }

    private record PropagationResult(boolean[] unbounded, int[] causingSeed) {
    }

    private static PropagationResult propagateUnbounded(Graph graph, boolean[] seed) {
        int n = graph.nodeCount();
        boolean[] unbounded = seed.clone();
        int[] causingSeed = new int[n];
        Arrays.fill(causingSeed, -1);

        List<List<Edge>> adjacency = graph.adjacencyList();
        Deque<Integer> queue = new ArrayDeque<>();
        for (int i = 0; i < n; i++) {
            if (seed[i]) {
                causingSeed[i] = i;
                queue.add(i);
            }
        }

        while (!queue.isEmpty()) {
            int current = queue.poll();
            for (Edge edge : adjacency.get(current)) {
                int next = edge.to();
                if (!unbounded[next]) {
                    unbounded[next] = true;
                    causingSeed[next] = causingSeed[current];
                    queue.add(next);
                }
            }
        }
        return new PropagationResult(unbounded, causingSeed);
    }
}