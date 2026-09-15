// io/Mission4Parser.java
package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.algorithms.mission4.Kruskal;

import java.util.ArrayList;
import java.util.List;

/** Nota: aquí las intersecciones se numeran de 1 a N (no de 0 a N-1, a diferencia de Misión 2/3). */
public final class Mission4Parser {

    private Mission4Parser() {
    }

    public static List<Mission4Case> parse(String rawInput) {
        InputTokenizer tokens = new InputTokenizer(rawInput);
        List<Mission4Case> cases = new ArrayList<>();

        int totalCases = tokens.nextInt();
        for (int caseIndex = 1; caseIndex <= totalCases; caseIndex++) {
            int nodeCount = tokens.nextInt();
            if (nodeCount <= 0) {
                throw new ParseException("Caso #%d: N debe ser positivo (N=%d)".formatted(caseIndex, nodeCount));
            }
            int cableCount = tokens.nextInt();
            List<Kruskal.Edge> edges = new ArrayList<>(cableCount);

            for (int i = 0; i < cableCount; i++) {
                int u = tokens.nextInt();
                int v = tokens.nextInt();
                long cost = tokens.nextLong();
                if (u < 1 || u > nodeCount || v < 1 || v > nodeCount) {
                    throw new ParseException("Caso #%d: cable (%d,%d) fuera de rango [1, %d]"
                            .formatted(caseIndex, u, v, nodeCount));
                }
                edges.add(new Kruskal.Edge(u, v, cost));
            }
            cases.add(new Mission4Case(nodeCount, edges));
        }
        return cases;
    }
}