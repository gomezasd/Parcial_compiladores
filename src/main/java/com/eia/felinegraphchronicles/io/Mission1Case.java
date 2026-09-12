package com.eia.felinegraphchronicles.io;

import com.eia.felinegraphchronicles.modelo.Grid;

/** Un caso de prueba ya parseado de la Misión 1: el grid con bombas, más el inicio y destino. */
public record Mission1Case(Grid grid, int startRow, int startCol, int destRow, int destCol) {
}