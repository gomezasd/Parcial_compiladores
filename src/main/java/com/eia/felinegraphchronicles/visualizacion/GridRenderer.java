package com.eia.felinegraphchronicles.visualizacion;

import com.eia.felinegraphchronicles.modelo.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dibuja el grid de la Misión 1, resaltando el camino encontrado.
 * Respeta el límite de la Sección 2.3: grids mayores a 50x50 no se
 * dibujan, solo se muestra un mensaje explicando por qué se omitió.
 */
public final class GridRenderer extends JPanel {

    private static final int MAX_DRAWABLE_DIMENSION = 50;

    private Grid grid;
    private List<int[]> path = List.of();

    public GridRenderer() {
        setPreferredSize(new Dimension(450, 450));
        setBackground(Color.WHITE);
    }

    public Grid getGrid() {
        return grid;
    }

    public void setData(Grid grid, List<int[]> path) {
        this.grid = grid;
        this.path = path != null ? path : List.of();
        repaint();
    }

    public void clear() {
        this.grid = null;
        this.path = List.of();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (grid == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (grid.rows() > MAX_DRAWABLE_DIMENSION || grid.cols() > MAX_DRAWABLE_DIMENSION) {
            drawSizeMessage(g2);
            return;
        }
        drawGrid(g2);
    }

    private void drawSizeMessage(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String message = "Grid %dx%d supera el limite de dibujo (50x50): se omite la visualizacion."
                .formatted(grid.rows(), grid.cols());
        g2.drawString(message, 10, 25);
    }

    private void drawGrid(Graphics2D g2) {
        int rows = grid.rows();
        int cols = grid.cols();
        int cellSize = Math.max(4, Math.min(getWidth() / cols, getHeight() / rows));

        // Set de celdas del camino, para consultar en O(1) si una celda
        // pertenece al camino mientras dibujamos cada una
        Set<Long> pathCells = new HashSet<>();
        for (int[] cell : path) {
            pathCells.add(encode(cell[0], cell[1], cols));
        }

        for (int r = 0; r < rows; r++) {
            for (int c = 0; c < cols; c++) {
                int x = c * cellSize;
                int y = r * cellSize;

                if (grid.isBomb(r, c)) {
                    g2.setColor(new Color(200, 60, 60)); // rojo = bomba
                } else if (pathCells.contains(encode(r, c, cols))) {
                    g2.setColor(new Color(60, 160, 110)); // verde = camino
                } else {
                    g2.setColor(new Color(235, 235, 235)); // gris claro = celda normal
                }
                g2.fillRect(x, y, cellSize, cellSize);
                g2.setColor(Color.LIGHT_GRAY);
                g2.drawRect(x, y, cellSize, cellSize);
            }
        }

        if (!path.isEmpty()) {
            int[] start = path.get(0);
            int[] dest = path.get(path.size() - 1);
            drawLabel(g2, start[0], start[1], cellSize, "S");
            drawLabel(g2, dest[0], dest[1], cellSize, "N");
        }
    }

    private void drawLabel(Graphics2D g2, int row, int col, int cellSize, String text) {
        g2.setColor(Color.BLACK);
        g2.setFont(new Font("SansSerif", Font.BOLD, Math.max(10, cellSize / 2)));
        FontMetrics fm = g2.getFontMetrics();
        int x = col * cellSize + (cellSize - fm.stringWidth(text)) / 2;
        int y = row * cellSize + (cellSize + fm.getAscent()) / 2 - 2;
        g2.drawString(text, x, y);
    }

    // Codifica (fila, columna) como un solo número, para usarlo como llave del Set
    private long encode(int row, int col, int cols) {
        return (long) row * cols + col;
    }
}
