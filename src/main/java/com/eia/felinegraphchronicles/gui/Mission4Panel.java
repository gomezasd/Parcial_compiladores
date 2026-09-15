// gui/Mission4Panel.java
package com.eia.felinegraphchronicles.gui;

import com.eia.felinegraphchronicles.algorithms.mission4.Kruskal;
import com.eia.felinegraphchronicles.io.Mission4Case;
import com.eia.felinegraphchronicles.io.Mission4Parser;
import com.eia.felinegraphchronicles.io.ParseException;
import com.eia.felinegraphchronicles.visualizacion.NetworkRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Mission4Panel extends JPanel {

    private static final String SAMPLE_INPUT = """
            1
            4
            5
            1 2 10
            2 3 20
            3 4 30
            4 1 40
            1 3 15
            """;

    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final NetworkRenderer networkRenderer = new NetworkRenderer();

    public Mission4Panel(MainFrame mainFrame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(buildHeader(mainFrame), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame mainFrame) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Misión 4: Reconectando la red (Kruskal)");
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

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, networkRenderer);
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
            List<Mission4Case> cases = Mission4Parser.parse(inputArea.getText());
            StringBuilder output = new StringBuilder();

            int caseNumber = 0;
            for (Mission4Case testCase : cases) {
                caseNumber++;
                Kruskal.Result result = Kruskal.solve(testCase.nodeCount(), testCase.edges());

                output.append(result.connected()
                        ? "Case #%d: %d".formatted(caseNumber, result.totalCost())
                        : "Case #%d: Limon cut too many cables".formatted(caseNumber)
                ).append("\n");

                if (caseNumber == 1) {
                    List<Kruskal.Edge> used = result.connected() ? result.usedEdges() : List.of();
                    networkRenderer.setData(testCase.nodeCount(), testCase.edges(), used);
                }
            }
            outputArea.setText(output.toString());
        } catch (ParseException ex) {
            outputArea.setText("Error de formato en el input:\n" + ex.getMessage());
            networkRenderer.clear();
        } catch (RuntimeException ex) {
            outputArea.setText("Error al procesar el input:\n" + ex.getMessage());
            networkRenderer.clear();
        }
    }
}
