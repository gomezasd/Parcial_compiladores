package com.eia.felinegraphchronicles.algorithms.mission2;

import com.eia.felinegraphchronicles.modelo.Edge;
import com.eia.felinegraphchronicles.modelo.Graph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

public final class Dijkstra {

    private Dijkstra() {
    }

    // IMPORTANTE: este "infinito" es propio de Dijkstra (positivo, para
    // minimización). NO reutilizar Sentinels.UNREACHABLE de Misión 3,
    // que es negativo y está pensado para maximización — mezclar los
    // dos rompe la condición de relajación (candidate < dist[v] nunca
    // se cumple si dist[v] arranca en un número muy negativo).
    private static final long INFINITY = Long.MAX_VALUE / 2;

    public record Result(long cost, List<Integer> path) {
        public boolean isReachable() {
            return cost != INFINITY;
        }
    }

    public static Result solve(Graph graph, int source, int destination) {
        int n = graph.nodeCount();
        long[] dist = new long[n];
        int[] predecessor = new int[n];
        Arrays.fill(dist, INFINITY);
        Arrays.fill(predecessor, -1);
        dist[source] = 0;

        if (source == destination) {
            return new Result(0, List.of(source));
        }

        List<List<Edge>> adjacency = graph.adjacencyList();
        boolean[] visited = new boolean[n];

        PriorityQueue<long[]> pq = new PriorityQueue<>(Comparator.comparingLong(a -> a[0]));
        pq.add(new long[]{0L, source});

        while (!pq.isEmpty()) {
            long[] top = pq.poll();
            int u = (int) top[1];
            if (visited[u]) continue;
            visited[u] = true;
            if (u == destination) break;

            for (Edge edge : adjacency.get(u)) {
                int v = edge.to();
                if (visited[v]) continue;
                long candidate = dist[u] + edge.weight();
                if (candidate < dist[v]) {
                    dist[v] = candidate;
                    predecessor[v] = u;
                    pq.add(new long[]{candidate, v});
                }
            }
        }

        if (dist[destination] == INFINITY) {
            return new Result(INFINITY, List.of());
        }
        return new Result(dist[destination], reconstructPath(predecessor, source, destination));
    }

    private static List<Integer> reconstructPath(int[] predecessor, int source, int destination) {
        List<Integer> path = new ArrayList<>();
        int current = destination;
        while (current != source) {
            path.add(current);
            current = predecessor[current];
        }
        path.add(source);
        Collections.reverse(path);
        return path;
    }
}