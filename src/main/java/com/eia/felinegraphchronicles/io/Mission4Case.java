package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.algorithms.mission4.Kruskal;

import java.util.List;

public record Mission4Case(int nodeCount, List<Kruskal.Edge> edges) {
}