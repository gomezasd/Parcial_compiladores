package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.algorithms.mission1.BfsSolver;
import com.eia.felinegraphchronicles.algorithms.mission1.DfsSolver;
import com.eia.felinegraphchronicles.modelo.resultado.PathResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class Mission1ParserEndToEndTest {

    // El texto EXACTO del sample input de la Misión 1, tal como aparece
    // en el PDF (usamos un "text block" de Java 17, entre """).
    private static final String SAMPLE_INPUT = """
            10 10
            9
            0 1 2
            1 1 2
            2 2 2 9
            3 2 1 7
            5 3 3 6 9
            6 4 0 1 2 7
            7 3 0 3 8
            8 2 7 9
            9 3 2 3 4
            0 0
            9 9
            0 0
            """;

    @Test
    void parseoCompletoDelSampleDaElResultadoEsperado() {
        List<Mission1Case> cases = Mission1Parser.parse(SAMPLE_INPUT);

        // El sample solo trae UN caso real (el terminador 0 0 no cuenta)
        assertEquals(1, cases.size());

        Mission1Case testCase = cases.get(0);

        PathResult bfs = BfsSolver.solve(
                testCase.grid(), testCase.startRow(), testCase.startCol(),
                testCase.destRow(), testCase.destCol());
        PathResult dfs = DfsSolver.solve(
                testCase.grid(), testCase.startRow(), testCase.startCol(),
                testCase.destRow(), testCase.destCol());

        // Esperado según el enunciado: "Case #1: BFS 18 DFS 32"
        assertTrue(bfs.isReachable());
        assertTrue(dfs.isReachable());
        assertEquals(18, bfs.moves());
        assertEquals(32, dfs.moves());
    }
}