package com.eia.felinegraphchronicles.visualizacion;

import com.eia.felinegraphchronicles.modelo.Edge;
import com.eia.felinegraphchronicles.modelo.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dibuja el grafo de la Misión 3 en disposición circular, resaltando
 * la ruta (verde) o el ciclo (naranja) según corresponda. Respeta el
 * límite de la Sección 2.3: grafos de más de 60 nodos no se dibujan.
 */
public final class GraphRenderer extends JPanel {

    private static final int MAX_DRAWABLE_NODES = 60;

    private Graph graph;
    private List<Integer> highlightNodes = List.of();
    private boolean highlightIsCycle;

    public GraphRenderer() {
        setPreferredSize(new Dimension(450, 450));
        setBackground(Color.WHITE);
    }

    public void setData(Graph graph, List<Integer> highlightNodes, boolean highlightIsCycle) {
        this.graph = graph;
        this.highlightNodes = highlightNodes != null ? highlightNodes : List.of();
        this.highlightIsCycle = highlightIsCycle;
        repaint();
    }

    public void clear() {
        this.graph = null;
        this.highlightNodes = List.of();
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (graph == null) return;

        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        if (graph.nodeCount() > MAX_DRAWABLE_NODES) {
            drawSizeMessage(g2);
            return;
        }

        Point[] positions = computeCircularLayout();
        Set<String> highlightedEdges = buildHighlightedEdgeSet();
        drawEdges(g2, positions, highlightedEdges);
        drawNodes(g2, positions);
    }

    private void drawSizeMessage(Graphics2D g2) {
        g2.setColor(Color.DARK_GRAY);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 13));
        String message = "Grafo de %d nodos supera el limite de dibujo (60): se omite la visualizacion."
                .formatted(graph.nodeCount());
        g2.drawString(message, 10, 25);
    }

    private Point[] computeCircularLayout() {
        int n = graph.nodeCount();
        Point[] positions = new Point[n];
        int cx = getWidth() / 2;
        int cy = getHeight() / 2;
        int radius = Math.max(50, Math.min(getWidth(), getHeight()) / 2 - 40);

        for (int i = 0; i < n; i++) {
            double angle = 2 * Math.PI * i / n;
            positions[i] = new Point(
                    cx + (int) (radius * Math.cos(angle)),
                    cy + (int) (radius * Math.sin(angle)));
        }
        return positions;
    }

    // Pares (from->to) que forman parte de la ruta o del ciclo a resaltar
    private Set<String> buildHighlightedEdgeSet() {
        Set<String> pairs = new HashSet<>();
        int size = highlightNodes.size();
        if (size < 2) return pairs;

        for (int i = 0; i < size - 1; i++) {
            pairs.add(highlightNodes.get(i) + "->" + highlightNodes.get(i + 1));
        }
        if (highlightIsCycle) {
            pairs.add(highlightNodes.get(size - 1) + "->" + highlightNodes.get(0));
        }
        return pairs;
    }

    private void drawEdges(Graphics2D g2, Point[] positions, Set<String> highlightedEdges) {
        for (Edge edge : graph.edges()) {
            boolean highlighted = highlightedEdges.contains(edge.from() + "->" + edge.to());
            g2.setColor(highlighted
                    ? (highlightIsCycle ? new Color(230, 140, 30) : new Color(60, 160, 110))
                    : new Color(210, 210, 210));
            g2.setStroke(new BasicStroke(highlighted ? 2.5f : 1f));
            drawArrow(g2, positions[edge.from()], positions[edge.to()]);
        }
    }

    private void drawArrow(Graphics2D g2, Point from, Point to) {
        g2.drawLine(from.x, from.y, to.x, to.y);
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        int arrowSize = 8;
        int x1 = (int) (to.x - arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (to.y - arrowSize * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (to.x - arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (to.y - arrowSize * Math.sin(angle + Math.PI / 6));
        g2.drawLine(to.x, to.y, x1, y1);
        g2.drawLine(to.x, to.y, x2, y2);
    }

    private void drawNodes(Graphics2D g2, Point[] positions) {
        int nodeRadius = 16;
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i < positions.length; i++) {
            Point p = positions[i];
            g2.setColor(new Color(70, 90, 200));
            g2.fillOval(p.x - nodeRadius, p.y - nodeRadius, nodeRadius * 2, nodeRadius * 2);
            g2.setColor(Color.WHITE);
            String label = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, p.x - fm.stringWidth(label) / 2, p.y + fm.getAscent() / 2 - 2);
        }
    }

    public Graph getGraph() {
        return graph;
    }
}
