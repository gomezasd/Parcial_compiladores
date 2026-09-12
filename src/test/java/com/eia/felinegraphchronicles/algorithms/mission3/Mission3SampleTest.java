package com.eia.felinegraphchronicles.algorithms.mission3;

import com.eia.felinegraphchronicles.algorithms.mission3.Mission3Orchestrator;
import com.eia.felinegraphchronicles.modelo.Graph;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Mission3SampleTest {

    // ---------- Caso #1 del enunciado: resultado finito = 110 ----------

    private Graph buildCase1() {
        // 5 nodos, grafo DIRIGIDO (true), tal como exige la Misión 3
        Graph graph = new Graph(5, true);
        graph.addEdge(0, 1, 50);
        graph.addEdge(0, 2, 10);
        graph.addEdge(1, 2, -30);
        graph.addEdge(1, 3, 40);
        graph.addEdge(2, 1, -5);
        graph.addEdge(2, 3, 60);
        graph.addEdge(3, 4, 20);
        return graph;
    }

    // ---------- Tests del orquestador, contra los 3 casos completos ----------

    @Test
    void orchestratorCase1DaMensajeCorrecto() {
        var result = Mission3Orchestrator.resolve(buildCase1(), 0, 4);
        assertEquals("110", Mission3Orchestrator.describe(result));
    }

    @Test
    void orchestratorCase2DaMensajeInfiniteChurun() {
        var result = Mission3Orchestrator.resolve(buildCase2(), 0, 3);
        assertEquals("Infinite churun!", Mission3Orchestrator.describe(result));
    }

    @Test
    void orchestratorCase3DaMensajeNegativo() {
        var result = Mission3Orchestrator.resolve(buildCase3(), 0, 2);
        assertEquals("-65", Mission3Orchestrator.describe(result));
    }

    @Test
    void orchestratorDetectaDestinoInalcanzable() {
        // Grafo de 3 nodos sin ninguna arista: 0 nunca puede llegar a 2
        Graph graph = new Graph(3, true);
        var result = Mission3Orchestrator.resolve(graph, 0, 2);
        assertEquals("Limon blocked the way", Mission3Orchestrator.describe(result));
    }

    @Test
    void floydWarshallCase1DaResultadoFinito() {
        Graph graph = buildCase1();
        long[][] matrix = FloydWarshallSolver.computeMaxMatrix(graph);

        // S=0, D=4 según el enunciado
        assertEquals(110, matrix[0][4]);
    }

    @Test
    void bellmanFordCase1DaResultadoFinito() {
        Graph graph = buildCase1();
        BellmanFordSolver.Result result = BellmanFordSolver.solve(graph, 0);

        assertFalse(result.unbounded[4]);
        assertEquals(110, result.dist[4]);
    }

    @Test
    void floydWarshallYBellmanFordCoincidenEnCaso1() {
        Graph graph = buildCase1();
        long[][] matrix = FloydWarshallSolver.computeMaxMatrix(graph);
        BellmanFordSolver.Result bf = BellmanFordSolver.solve(graph, 0);

        // El cross-check que exige el enunciado: ambos algoritmos deben coincidir
        assertEquals(matrix[0][4], bf.dist[4]);
    }

    // ---------- Caso #2 del enunciado: "Infinite churun!" ----------

    private Graph buildCase2() {
        Graph graph = new Graph(4, true);
        graph.addEdge(0, 1, 20);
        graph.addEdge(1, 2, 30);
        graph.addEdge(2, 1, -10); // junto con 1->2 forma un ciclo con ganancia +20
        graph.addEdge(2, 3, 15);
        return graph;
    }

    @Test
    void floydWarshallDetectaInfinitoEnCaso2() {
        Graph graph = buildCase2();
        long[][] matrix = FloydWarshallSolver.computeMaxMatrix(graph);

        // S=0, D=3: debe marcarse como "unbounded" (Long.MAX_VALUE = infinito)
        assertEquals(Long.MAX_VALUE, matrix[0][3]);
    }

    @Test
    void bellmanFordDetectaInfinitoEnCaso2() {
        Graph graph = buildCase2();
        BellmanFordSolver.Result result = BellmanFordSolver.solve(graph, 0);

        assertTrue(result.unbounded[3]);
    }

    // ---------- Caso #3 del enunciado: resultado negativo = -65 ----------

    private Graph buildCase3() {
        Graph graph = new Graph(3, true);
        graph.addEdge(0, 1, -40);
        graph.addEdge(1, 2, -25);
        graph.addEdge(0, 2, -80);
        return graph;
    }

    @Test
    void floydWarshallAceptaResultadoNegativoEnCaso3() {
        Graph graph = buildCase3();
        long[][] matrix = FloydWarshallSolver.computeMaxMatrix(graph);

        assertEquals(-65, matrix[0][2]);
    }

    @Test
    void bellmanFordAceptaResultadoNegativoEnCaso3() {
        Graph graph = buildCase3();
        BellmanFordSolver.Result result = BellmanFordSolver.solve(graph, 0);

        assertFalse(result.unbounded[2]);
        assertEquals(-65, result.dist[2]);
    }
}