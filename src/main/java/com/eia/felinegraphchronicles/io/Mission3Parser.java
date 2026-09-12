package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.modelo.Graph;

import java.util.ArrayList;
import java.util.List;

/**
 * Traduce el texto de input de la Misión 3 (formato descrito en la
 * Sección 5 del enunciado) a una lista de Mission3Case, uno por cada
 * uno de los T test cases declarados en la primera línea.
 */
public final class Mission3Parser {

    private Mission3Parser() {
    }

    public static List<Mission3Case> parse(String rawInput) {
        InputTokenizer tokens = new InputTokenizer(rawInput);
        List<Mission3Case> cases = new ArrayList<>();

        int totalCases = tokens.nextInt();

        for (int caseIndex = 1; caseIndex <= totalCases; caseIndex++) {
            int nodeCount = tokens.nextInt();
            int passageCount = tokens.nextInt();
            int source = tokens.nextInt();
            int destination = tokens.nextInt();

            if (nodeCount <= 0) {
                throw new ParseException(
                        "Caso #%d: N debe ser positivo (N=%d)".formatted(caseIndex, nodeCount));
            }

            // El grafo de Misión 3 es DIRIGIDO, a diferencia del de Misión 2/4
            Graph graph = new Graph(nodeCount, true);

            for (int i = 0; i < passageCount; i++) {
                int from = tokens.nextInt();
                int to = tokens.nextInt();
                long weight = tokens.nextLong(); // puede ser negativo, por eso long y no int sin más
                graph.addEdge(from, to, weight);
            }

            cases.add(new Mission3Case(graph, source, destination));
        }

        return cases;
    }
}
