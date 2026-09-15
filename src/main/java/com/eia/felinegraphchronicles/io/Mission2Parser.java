package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.modelo.Graph;

import java.util.ArrayList;
import java.util.List;

public final class Mission2Parser {

    private Mission2Parser() {
    }

    public static List<Mission2Case> parse(String rawInput) {
        InputTokenizer tokens = new InputTokenizer(rawInput);
        List<Mission2Case> cases = new ArrayList<>();

        int totalCases = tokens.nextInt();
        for (int caseIndex = 1; caseIndex <= totalCases; caseIndex++) {
            int nodeCount = tokens.nextInt();
            int connectionCount = tokens.nextInt();
            int source = tokens.nextInt();
            int destination = tokens.nextInt();

            if (nodeCount <= 0) {
                throw new ParseException("Caso #%d: N debe ser positivo (N=%d)".formatted(caseIndex, nodeCount));
            }
            if (source < 0 || source >= nodeCount || destination < 0 || destination >= nodeCount) {
                throw new ParseException("Caso #%d: S=%d o D=%d fuera de rango [0, %d)"
                        .formatted(caseIndex, source, destination, nodeCount));
            }

            Graph graph = new Graph(nodeCount, false); // Misión 2: bidireccional

            for (int i = 0; i < connectionCount; i++) {
                int a = tokens.nextInt();
                int b = tokens.nextInt();
                long weight = tokens.nextLong();
                if (a < 0 || a >= nodeCount || b < 0 || b >= nodeCount) {
                    throw new ParseException("Caso #%d: conexion (%d,%d) fuera de rango [0, %d)"
                            .formatted(caseIndex, a, b, nodeCount));
                }
                graph.addEdge(a, b, weight);
            }

            cases.add(new Mission2Case(graph, source, destination));
        }
        return cases;
    }
}