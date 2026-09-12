package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.modelo.Graph;

/** Un caso de prueba ya parseado de la Misión 3: el grafo dirigido, más el origen y destino. */
public record Mission3Case(Graph graph, int source, int destination) {
}
