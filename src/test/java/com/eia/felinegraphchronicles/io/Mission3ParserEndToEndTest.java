package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.algorithms.mission3.Mission3Orchestrator;
import com.eia.felinegraphchronicles.modelo.resultado.MaxWalkResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Mission3ParserEndToEndTest {

    // El texto EXACTO del sample input de la Misión 3, tal como aparece
    // en el PDF: 3 casos de prueba (T=3).
    private static final String SAMPLE_INPUT = """
            3
            5 7 0 4
            0 1 50
            0 2 10
            1 2 -30
            1 3 40
            2 1 -5
            2 3 60
            3 4 20
            4 4 0 3
            0 1 20
            1 2 30
            2 1 -10
            2 3 15
            3 3 0 2
            0 1 -40
            1 2 -25
            0 2 -80
            """;

    @Test
    void parseoCompletoDelSampleDaLosTresResultadosEsperados() {
        List<Mission3Case> cases = Mission3Parser.parse(SAMPLE_INPUT);

        // El sample trae 3 casos (T=3)
        assertEquals(3, cases.size());

        // Caso #1 esperado: "110"
        MaxWalkResult result1 = Mission3Orchestrator.resolve(
                cases.get(0).graph(), cases.get(0).source(), cases.get(0).destination());
        assertEquals("110", Mission3Orchestrator.describe(result1));

        // Caso #2 esperado: "Infinite churun!"
        MaxWalkResult result2 = Mission3Orchestrator.resolve(
                cases.get(1).graph(), cases.get(1).source(), cases.get(1).destination());
        assertEquals("Infinite churun!", Mission3Orchestrator.describe(result2));

        // Caso #3 esperado: "-65"
        MaxWalkResult result3 = Mission3Orchestrator.resolve(
                cases.get(2).graph(), cases.get(2).source(), cases.get(2).destination());
        assertEquals("-65", Mission3Orchestrator.describe(result3));
    }
}