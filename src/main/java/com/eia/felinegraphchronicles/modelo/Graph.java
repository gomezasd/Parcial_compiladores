package com.eia.felinegraphchronicles.modelo;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Representa un grafo genérico: un conjunto de nodos (numerados de 0 a
 * nodeCount-1) conectados por aristas. Lo van a USAR las Misiones 2, 3 y 4,
 * cada una construyendo su propio Graph a partir de su input.
 *
 * "directed" indica si las conexiones son de un solo sentido (Misión 3,
 * pasajes de A a B) o de doble sentido (Misiones 2 y 4, conexiones
 * bidireccionales).
 */
public final class Graph {

    // Cantidad total de nodos del grafo (fija)
    private final int nodeCount;

    // true = grafo dirigido (A->B distinto de B->A), false = no dirigido
    private final boolean directed;

    // Lista simple con TODAS las aristas, en el orden en que se agregaron.
    // No eliminamos duplicados ni self-loops (conexión de un nodo a sí
    // mismo) porque el enunciado pide tolerarlos, no descartarlos.
    private final List<Edge> edges = new ArrayList<>();

    public Graph(int nodeCount, boolean directed) {
        if (nodeCount <= 0) {
            throw new IllegalArgumentException("nodeCount debe ser positivo");
        }
        this.nodeCount = nodeCount;
        this.directed = directed;
    }

    // Agrega una arista nueva al grafo
    public void addEdge(int from, int to, long weight) {
        requireValidNode(from);
        requireValidNode(to);
        edges.add(new Edge(from, to, weight));
    }

    public int nodeCount() {
        return nodeCount;
    }

    public boolean isDirected() {
        return directed;
    }

    // Devuelve la lista de aristas, pero "unmodifiableList" evita que
    // quien la reciba pueda modificarla por accidente desde afuera
    public List<Edge> edges() {
        return Collections.unmodifiableList(edges);
    }

    /**
     * Construye una LISTA DE ADYACENCIA: para cada nodo, la lista de
     * aristas que salen de él. Es la estructura que Dijkstra necesita
     * para recorrer el grafo eficientemente.
     *
     * Si el grafo NO es dirigido, cada arista se agrega en ambos
     * sentidos (A->B y B->A), porque se puede caminar en cualquier
     * dirección.
     *
     * Se recalcula cada vez que la llamas (no se guarda en memoria),
     * para evitar que quede desactualizada si el grafo cambia.
     */
    public List<List<Edge>> adjacencyList() {
        List<List<Edge>> adjacency = new ArrayList<>(nodeCount);

        // Primero creamos una lista vacía por cada nodo
        for (int i = 0; i < nodeCount; i++) {
            adjacency.add(new ArrayList<>());
        }

        // Luego recorremos todas las aristas y las "colgamos" del nodo
        // correspondiente
        for (Edge edge : edges) {
            adjacency.get(edge.from()).add(edge);
            if (!directed) {
                // Si es no dirigido, agregamos también el sentido contrario
                adjacency.get(edge.to()).add(new Edge(edge.to(), edge.from(), edge.weight()));
            }
        }
        return adjacency;
    }

    // Valida que un número de nodo esté dentro del rango permitido
    private void requireValidNode(int node) {
        if (node < 0 || node >= nodeCount) {
            throw new IndexOutOfBoundsException(
                    "nodo %d fuera de rango [0, %d)".formatted(node, nodeCount));
        }
    }
}
