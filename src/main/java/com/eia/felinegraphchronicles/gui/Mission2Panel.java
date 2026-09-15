// gui/Mission2Panel.java
package com.eia.felinegraphchronicles.gui;

import com.eia.felinegraphchronicles.algorithms.mission2.Dijkstra;
import com.eia.felinegraphchronicles.io.Mission2Case;
import com.eia.felinegraphchronicles.io.Mission2Parser;
import com.eia.felinegraphchronicles.io.ParseException;
import com.eia.felinegraphchronicles.visualizacion.GraphRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Mission2Panel extends JPanel {

    private static final String SAMPLE_INPUT = """
            3
            2 1 0 1
            0 1 100
            3 3 2 0
            0 1 100
            0 2 200
            1 2 50
            2 0 0 1
            """;

    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final GraphRenderer graphRenderer = new GraphRenderer();

    public Mission2Panel(MainFrame mainFrame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        graphRenderer.setDirected(false); // conexiones bidireccionales
        add(buildHeader(mainFrame), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame mainFrame) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Misión 2: Recuperando las cuentas de Claude (Dijkstra)");
        title.setFont(new Font("SansSerif", Font.BOLD, 16));
        JButton backButton = new JButton("← Volver al menú");
        backButton.addActionListener(e -> mainFrame.showMenu());
        header.add(title, BorderLayout.WEST);
        header.add(backButton, BorderLayout.EAST);
        return header;
    }

    private JSplitPane buildBody() {
        JPanel inputPanel = new JPanel(new BorderLayout(5, 5));
        inputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        inputPanel.add(new JLabel("Input:"), BorderLayout.NORTH);
        inputPanel.add(new JScrollPane(inputArea), BorderLayout.CENTER);
        inputPanel.add(buildInputButtons(), BorderLayout.SOUTH);

        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputPanel.add(new JLabel("Resultado:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, graphRenderer);
        rightSplit.setResizeWeight(0.3);

        JSplitPane mainSplit = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, inputPanel, rightSplit);
        mainSplit.setResizeWeight(0.4);
        return mainSplit;
    }

    private JPanel buildInputButtons() {
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.LEFT));
        JButton loadSampleButton = new JButton("Cargar ejemplo");
        loadSampleButton.addActionListener(e -> inputArea.setText(SAMPLE_INPUT));
        JButton solveButton = new JButton("Resolver");
        solveButton.addActionListener(e -> solve());
        buttons.add(loadSampleButton);
        buttons.add(solveButton);
        return buttons;
    }

    private void solve() {
        try {
            List<Mission2Case> cases = Mission2Parser.parse(inputArea.getText());
            StringBuilder output = new StringBuilder();

            int caseNumber = 0;
            for (Mission2Case testCase : cases) {
                caseNumber++;
                Dijkstra.Result result = Dijkstra.solve(testCase.graph(), testCase.source(), testCase.destination());

                output.append(result.isReachable()
                        ? "Case #%d: %d".formatted(caseNumber, result.cost())
                        : "Case #%d: Nina is very sad".formatted(caseNumber)
                ).append("\n");

                if (caseNumber == 1) {
                    List<Integer> highlight = result.isReachable() ? result.path() : List.of();
                    graphRenderer.setData(testCase.graph(), highlight, false);
                }
            }
            outputArea.setText(output.toString());
        } catch (ParseException ex) {
            outputArea.setText("Error de formato en el input:\n" + ex.getMessage());
            graphRenderer.clear();
        } catch (RuntimeException ex) {
            outputArea.setText("Error al procesar el input:\n" + ex.getMessage());
            graphRenderer.clear();
        }
    }
}