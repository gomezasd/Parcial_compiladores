package com.eia.felinegraphchronicles.visualizacion;

import com.eia.felinegraphchronicles.modelo.Edge;
import com.eia.felinegraphchronicles.modelo.Graph;

import javax.swing.*;
import java.awt.*;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Dibuja un grafo en disposición circular. Misión 3 lo usa dirigido
 * (con flechas, resaltando ruta o ciclo); Misión 2 lo usa NO dirigido
 * (sin flechas, resaltando la ruta más corta) llamando setDirected(false).
 * Respeta el límite de la Sección 2.3: más de 60 nodos omite el dibujo.
 */
public final class GraphRenderer extends JPanel {

    private static final int MAX_DRAWABLE_NODES = 60;
    private static final int NODE_RADIUS = 16;

    private Graph graph;
    private List<Integer> highlightNodes = List.of();
    private boolean highlightIsCycle;
    private boolean directed = true;

    public GraphRenderer() {
        setPreferredSize(new Dimension(450, 450));
        setBackground(Color.WHITE);
    }

    public void setDirected(boolean directed) {
        this.directed = directed;
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

    public Graph getGraph() {
        return graph;
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

    // Dirigido: respeta el sentido from->to. No dirigido: usa un par sin
    // sentido, porque la ruta puede "recorrer" el par en cualquier dirección
    // y graph.edges() solo guarda la arista en el sentido en que se agregó.
    private String edgeKey(int from, int to) {
        return directed ? (from + "->" + to) : pairKey(from, to);
    }

    private Set<String> buildHighlightedEdgeSet() {
        Set<String> pairs = new HashSet<>();
        int size = highlightNodes.size();
        if (size < 2) return pairs;
        for (int i = 0; i < size - 1; i++) {
            pairs.add(edgeKey(highlightNodes.get(i), highlightNodes.get(i + 1)));
        }
        if (highlightIsCycle) {
            pairs.add(edgeKey(highlightNodes.get(size - 1), highlightNodes.get(0)));
        }
        return pairs;
    }

    private void drawEdges(Graphics2D g2, Point[] positions, Set<String> highlightedEdges) {
        Set<String> bidirectionalPairs = findBidirectionalPairs();

        for (Edge edge : graph.edges()) {
            boolean highlighted = highlightedEdges.contains(edgeKey(edge.from(), edge.to()));
            g2.setColor(highlighted
                    ? (highlightIsCycle ? new Color(230, 140, 30) : new Color(60, 160, 110))
                    : new Color(210, 210, 210));
            g2.setStroke(new BasicStroke(highlighted ? 2.5f : 1f));

            Point from = positions[edge.from()];
            Point to = positions[edge.to()];
            double offset = directed && bidirectionalPairs.contains(pairKey(edge.from(), edge.to())) ? 8 : 0;

            drawArrow(g2, from, to, offset);
            drawWeightLabel(g2, from, to, edge.weight(), highlighted, offset);
        }
    }

    private void drawArrow(Graphics2D g2, Point from, Point to, double offset) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        double perpAngle = angle + Math.PI / 2;
        int offX = (int) (offset * Math.cos(perpAngle));
        int offY = (int) (offset * Math.sin(perpAngle));

        int fx = from.x + offX, fy = from.y + offY;
        int tx = to.x + offX, ty = to.y + offY;

        int endX = (int) (tx - NODE_RADIUS * Math.cos(angle));
        int endY = (int) (ty - NODE_RADIUS * Math.sin(angle));
        int startX = (int) (fx + NODE_RADIUS * Math.cos(angle));
        int startY = (int) (fy + NODE_RADIUS * Math.sin(angle));

        g2.drawLine(startX, startY, endX, endY);
        if (!directed) return; // Misión 2: sin cabeza de flecha

        int arrowSize = 10;
        int x1 = (int) (endX - arrowSize * Math.cos(angle - Math.PI / 6));
        int y1 = (int) (endY - arrowSize * Math.sin(angle - Math.PI / 6));
        int x2 = (int) (endX - arrowSize * Math.cos(angle + Math.PI / 6));
        int y2 = (int) (endY - arrowSize * Math.sin(angle + Math.PI / 6));
        g2.drawLine(endX, endY, x1, y1);
        g2.drawLine(endX, endY, x2, y2);
    }

    private void drawWeightLabel(Graphics2D g2, Point from, Point to, long weight, boolean highlighted, double offset) {
        double angle = Math.atan2(to.y - from.y, to.x - from.x);
        double perpAngle = angle + Math.PI / 2;
        int midX = (from.x + to.x) / 2 + (int) (offset * Math.cos(perpAngle));
        int midY = (from.y + to.y) / 2 + (int) (offset * Math.sin(perpAngle));

        String text = String.valueOf(weight);
        g2.setFont(new Font("SansSerif", Font.PLAIN, 11));
        FontMetrics fm = g2.getFontMetrics();
        int textWidth = fm.stringWidth(text);
        int textHeight = fm.getHeight();

        g2.setColor(Color.WHITE);
        g2.fillRect(midX - textWidth / 2 - 2, midY - textHeight / 2, textWidth + 4, textHeight);
        g2.setColor(highlighted ? Color.BLACK : new Color(120, 120, 120));
        g2.drawString(text, midX - textWidth / 2, midY + fm.getAscent() / 2 - 2);
    }

    private void drawNodes(Graphics2D g2, Point[] positions) {
        g2.setFont(new Font("SansSerif", Font.BOLD, 12));
        for (int i = 0; i < positions.length; i++) {
            Point p = positions[i];
            g2.setColor(new Color(70, 90, 200));
            g2.fillOval(p.x - NODE_RADIUS, p.y - NODE_RADIUS, NODE_RADIUS * 2, NODE_RADIUS * 2);
            g2.setColor(Color.WHITE);
            String label = String.valueOf(i);
            FontMetrics fm = g2.getFontMetrics();
            g2.drawString(label, p.x - fm.stringWidth(label) / 2, p.y + fm.getAscent() / 2 - 2);
        }
    }

    private Set<String> findBidirectionalPairs() {
        Set<String> existing = new HashSet<>();
        for (Edge edge : graph.edges()) existing.add(edge.from() + "," + edge.to());
        Set<String> bidirectional = new HashSet<>();
        for (Edge edge : graph.edges()) {
            if (existing.contains(edge.to() + "," + edge.from())) {
                bidirectional.add(pairKey(edge.from(), edge.to()));
            }
        }
        return bidirectional;
    }

    private String pairKey(int a, int b) {
        return Math.min(a, b) + "-" + Math.max(a, b);
    }
}