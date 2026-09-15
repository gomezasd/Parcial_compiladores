package com.eia.felinegraphchronicles.visualizacion;

import com.eia.felinegraphchronicles.algorithms.mission4.Kruskal;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dibuja la red de intersecciones/cables de la Misión 4: verde sólido
 * para los cables del MST, gris punteado para los descartados (igual
 * que la ilustración de la Sección 6). Respeta el límite de la
 * Sección 2.3: más de 100 intersecciones o 300 cables omite el dibujo.
 */
public final class NetworkRenderer extends JPanel {

    private static final int MAX_NODES = 100;
    private static final int MAX_CABLES = 300;
    private static final int NODE_RADIUS = 14;

    private int nodeCount;
    private List<Kruskal.Edge> allEdges = List.of();
    private Set<Kruskal.Edge> usedEdges = Set.of();

    public NetworkRenderer() {
        setPreferredSize(new Dimension(450, 450));
        setBackground(Color.WHITE);
    }

    public void setData(int nodeCount, List<Kruskal.Edge> allEdges, List<Kruskal.Edge> usedEdges) {
        this.nodeCount = nodeCount;
        this.allEdges = allEdges != null ? allEdges : List.of();
        this.usedEdges = usedEdges != null ? new HashSet<>(usedEdges) : Set.of();
        repaint();
    }

    public void clear() {
        this.nodeCount = 0;
        this.allEdges = List.of();
        this.usedEdges = Set.of();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (nodeCount == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (nodeCount > MAX_NODES || allEdges.size() > MAX_CABLES) {
            drawSizeMessage(g2);
            return;
        }

        Point[] positions = computeCircularLayout();
        drawEdges(g2, positions);
        drawNodes(g2, positions);
    }

    private void drawSizeMessage(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String message = "Red de %d intersecciones / %d cables supera el limite de dibujo (100/300): se omite la visualizacion."
                .formatted(nodeCount, allEdges.size());
        g2.drawString(message, 10, 25);
    }

    // Índices 1..nodeCount, porque las intersecciones se numeran desde 1
    private Point[] computeCircularLayout() {
        Point[] positions = new Point[nodeCount + 1];
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.max(50, Math.min(getWidth(), getHeight()) / 2 - 40);
        for (int i = 1; i <= nodeCount; i++) {
            double angle = 2 * Math.PI * (i - 1) / nodeCount;
            positions[i] = new Point(
                    cx + (int) (radius * Math.cos(angle)),
                    cy + (int) (radius * Math.sin(angle)));
        }
        return positions;
    }

    private void drawEdges(Graphics2D g2, Point[] positions) {
        for (Kruskal.Edge edge : allEdges) {
            boolean used = usedEdges.contains(edge);
            Point from = positions[edge.u()];
            Point to = positions[edge.v()];

            g2.setColor(used ? new Color(60, 160, 110) : new Color(190, 190, 190));
            g2.setStroke(used
                    ? new BasicStroke(2.5f)
                    : new BasicStroke(1f, BasicStroke.CAP_BUTT, BasicStroke.JOIN_MITER, 1f, new float[]{4f, 4f}, 0f));
            g2.drawLine(from.x, from.y, to.x, to.y);
            drawWeightLabel(g2, from, to, edge.cost(), used);
        }
        g2.setStroke(new BasicStroke(1f));
    }

    private void drawWeightLabel(Graphics2D g2, Point from, Point to, long cost, boolean used) {
        int midX = (from.x + to.x) / 2;
        int midY = (from.y + to.y) / 2;
        String text = String.valueOf(cost);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);

        g2.setColor(Color.WHITE);
        g2.fillRect(midX - textWidth / 2 - 2, midY - fm.getHeight() / 2, textWidth + 4, fm.getHeight());
        g2.setColor(used ? Color.BLACK : new Color(130, 130, 130));
        g2.drawString(text, midX - textWidth / 2, midY + fm.getAscent() / 2 - 2);
    }

    private void drawNodes(Graphics2D g2, Point[] positions) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 1; i <= nodeCount; i++) {
            Point p = positions[i];
            g2.setColor(new Color(70, 90, 200));
            g2.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2.setColor(Color.WHITE);
            String label = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, p.x - fm.stringWidth(label) / 2, p.y + fm.getAscent() / 2 - 2);
        }
    }
}