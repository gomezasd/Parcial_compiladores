package com.eia.felinegraphchronicles.modelo;

/**
 * Representa una conexión (arista) entre dos nodos, con un peso o costo.
 * Es un "record" de Java 17: una forma corta de crear una clase inmutable
 * (una vez creada, no se puede modificar) que ya trae automáticamente
 * los métodos getters, equals, hashCode y toString.
 *
 * from  = nodo de origen
 * to    = nodo de destino
 * weight = peso/costo de la conexión (usamos long, no int, porque el
 *          enunciado exige acumular pesos grandes sin desbordarse)
 */
public record Edge(int from, int to, long weight) implements Comparable<Edge> {

    // Este método permite que Java sepa cómo ORDENAR una lista de aristas
    // (lo vas a necesitar en Kruskal, que exige ordenar por peso ascendente).
    @Override
    public int compareTo(Edge other) {
        return Long.compare(this.weight, other.weight);
    }
}


