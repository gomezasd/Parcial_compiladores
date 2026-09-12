package com.eia.felinegraphchronicles.modelo.resultado;

import java.util.List;

/**
 * Resultado de Floyd-Warshall/Bellman-Ford: el máximo churun entre dos
 * nodos, o alguno de los dos casos especiales que exige el enunciado.
 */
public final class MaxWalkResult {

    // Los 3 casos posibles, en el mismo orden de precedencia del enunciado
    public enum Status { UNREACHABLE, UNBOUNDED, OK }

    private final Status status;
    private final long value;          // solo tiene sentido si status == OK
    private final List<Integer> nodes; // ruta (OK) o ciclo (UNBOUNDED), para dibujar

    private MaxWalkResult(Status status, long value, List<Integer> nodes) {
        this.status = status;
        this.value = value;
        this.nodes = nodes;
    }

    public static MaxWalkResult unreachable() {
        return new MaxWalkResult(Status.UNREACHABLE, 0, List.of());
    }

    public static MaxWalkResult unbounded(List<Integer> cycleNodes) {
        return new MaxWalkResult(Status.UNBOUNDED, 0, cycleNodes);
    }

    public static MaxWalkResult of(long value, List<Integer> routeNodes) {
        return new MaxWalkResult(Status.OK, value, routeNodes);
    }

    public Status status() { return status; }
    public long value() { return value; }
    public List<Integer> nodes() { return nodes; }
}
