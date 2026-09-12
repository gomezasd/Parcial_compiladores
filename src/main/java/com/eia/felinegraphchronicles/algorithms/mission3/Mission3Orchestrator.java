package com.eia.felinegraphchronicles.algorithms.mission3;

import com.eia.felinegraphchronicles.modelo.Graph;
import com.eia.felinegraphchronicles.modelo.resultado.MaxWalkResult;
import com.eia.felinegraphchronicles.util.Sentinels;

import java.util.List;

public final class Mission3Orchestrator {

    private Mission3Orchestrator() {
    }

    public static final class CrossCheckMismatchException extends RuntimeException {
        public CrossCheckMismatchException(String message) {
            super(message);
        }
    }

    public static MaxWalkResult resolve(Graph graph, int source, int destination) {
        long[][] matrix = FloydWarshallSolver.computeMaxMatrix(graph);
        BellmanFordSolver.Result bellmanFord = BellmanFordSolver.solve(graph, source);

        MaxWalkResult fromFloydWarshall = toResult(matrix[source][destination]);
        MaxWalkResult fromBellmanFord = toResult(bellmanFord, source, destination, graph.nodeCount());

        validateCrossCheck(fromFloydWarshall, fromBellmanFord, source, destination);

        // Devolvemos el de Bellman-Ford: ya validamos que coincide en
        // estado/valor con Floyd-Warshall, y este además trae la ruta o
        // el ciclo reconstruido, necesario para la visualización.
        return fromBellmanFord;
    }

    private static MaxWalkResult toResult(long matrixValue) {
        if (matrixValue == Sentinels.NO_ROUTE) return MaxWalkResult.unreachable();
        if (matrixValue == Long.MAX_VALUE) return MaxWalkResult.unbounded(List.of());
        return MaxWalkResult.of(matrixValue, List.of());
    }

    private static MaxWalkResult toResult(BellmanFordSolver.Result bellmanFord, int source, int destination, int nodeCount) {
        if (bellmanFord.dist[destination] == Sentinels.UNREACHABLE) {
            return MaxWalkResult.unreachable();
        }
        if (bellmanFord.unbounded[destination]) {
            List<Integer> cycle = bellmanFord.reconstructCycle(destination, nodeCount);
            return MaxWalkResult.unbounded(cycle);
        }
        List<Integer> path = bellmanFord.reconstructPath(source, destination);
        return MaxWalkResult.of(bellmanFord.dist[destination], path);
    }

    private static void validateCrossCheck(MaxWalkResult a, MaxWalkResult b, int source, int destination) {
        boolean sameStatus = a.status() == b.status();
        boolean sameValue = a.status() != MaxWalkResult.Status.OK || a.value() == b.value();
        if (!sameStatus || !sameValue) {
            throw new CrossCheckMismatchException(
                    "Floyd-Warshall (%s) y Bellman-Ford (%s) no coinciden para S=%d, D=%d"
                            .formatted(a, b, source, destination));
        }
    }

    public static String describe(MaxWalkResult result) {
        return switch (result.status()) {
            case UNREACHABLE -> "Limon blocked the way";
            case UNBOUNDED -> "Infinite churun!";
            case OK -> Long.toString(result.value());
        };
    }
}