package com.eia.felinegraphchronicles.modelo;

/**
 * Representa el mapa/cuadrícula de la Misión 1: un rectángulo de "rows"
 * filas por "cols" columnas, donde algunas celdas tienen una bomba.
 */
public final class Grid {

    // Cantidad de filas y columnas del mapa (fijas, no cambian después de crear el Grid)
    private final int rows;
    private final int cols;

    // Matriz booleana: bomb[fila][columna] = true significa "aquí hay una bomba"
    private final boolean[][] bomb;

    // Constructor: crea un Grid vacío (sin bombas) del tamaño indicado
    public Grid(int rows, int cols) {
        if (rows <= 0 || cols <= 0) {
            // Protección: no tiene sentido un grid de 0 filas o negativo
            throw new IllegalArgumentException("rows y cols deben ser positivos");
        }
        this.rows = rows;
        this.cols = cols;
        // Java inicializa automáticamente todo boolean[][] en "false"
        this.bomb = new boolean[rows][cols];
    }

    // Marca la celda (row, col) como que tiene una bomba
    public void markBomb(int row, int col) {
        requireInBounds(row, col); // primero valida que la celda exista
        bomb[row][col] = true;
    }

    // Pregunta si la celda (row, col) tiene una bomba
    public boolean isBomb(int row, int col) {
        requireInBounds(row, col);
        return bomb[row][col];
    }

    // Pregunta si una posición está DENTRO del grid (útil al mover BFS/DFS
    // hacia arriba/abajo/izquierda/derecha, para no salirte del mapa)
    public boolean inBounds(int row, int col) {
        return row >= 0 && row < rows && col >= 0 && col < cols;
    }

    public int rows() {
        return rows;
    }

    public int cols() {
        return cols;
    }

    // Método privado auxiliar: lanza un error claro si alguien pide
    // una celda que no existe (en vez de fallar de forma confusa)
    private void requireInBounds(int row, int col) {
        if (!inBounds(row, col)) {
            throw new IndexOutOfBoundsException(
                    "(%d, %d) fuera del grid %dx%d".formatted(row, col, rows, cols));
        }
    }
}
