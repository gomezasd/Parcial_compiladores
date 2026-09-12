package com.eia.felinegraphchronicles.gui;

import com.eia.felinegraphchronicles.algorithms.mission3.FloydWarshallSolver;
import com.eia.felinegraphchronicles.algorithms.mission3.Mission3Orchestrator;
import com.eia.felinegraphchronicles.io.Mission3Case;
import com.eia.felinegraphchronicles.io.Mission3Parser;
import com.eia.felinegraphchronicles.io.ParseException;
import com.eia.felinegraphchronicles.modelo.resultado.MaxWalkResult;
import com.eia.felinegraphchronicles.visualizacion.GraphRenderer;
import com.eia.felinegraphchronicles.visualizacion.MatrixRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Mission3Panel extends JPanel {

    private static final String SAMPLE_INPUT = """
            3
            5 7 0 4
            0 1 50
            0 2 10
            1 2 -30
            1 3 40
            2 1 -5
            2 3 60
            3 4 20
            4 4 0 3
            0 1 20
            1 2 30
            2 1 -10
            2 3 15
            3 3 0 2
            0 1 -40
            1 2 -25
            0 2 -80
            """;

    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final GraphRenderer graphRenderer = new GraphRenderer();
    private final JPanel matrixContainer = new JPanel(new BorderLayout());

    public Mission3Panel(MainFrame mainFrame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
        add(buildHeader(mainFrame), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);
    }

    private JPanel buildHeader(MainFrame mainFrame) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Misión 3: El alijo de churun (Floyd-Warshall & Bellman-Ford)");
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

        // Pestañas: grafo con la ruta/ciclo resaltado, y la matriz NxN
        JTabbedPane visualizationTabs = new JTabbedPane();
        visualizationTabs.addTab("Grafo", graphRenderer);
        visualizationTabs.addTab("Matriz", matrixContainer);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, visualizationTabs);
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
            List<Mission3Case> cases = Mission3Parser.parse(inputArea.getText());
            StringBuilder output = new StringBuilder();

            int caseNumber = 0;
            for (Mission3Case testCase : cases) {
                caseNumber++;
                MaxWalkResult result = Mission3Orchestrator.resolve(
                        testCase.graph(), testCase.source(), testCase.destination());
                output.append("Case #%d: %s".formatted(caseNumber, Mission3Orchestrator.describe(result))).append("\n");

                // Solo dibujamos el PRIMER caso (mismo criterio que Misión 1)
                if (caseNumber == 1) {
                    boolean isCycle = result.status() == MaxWalkResult.Status.UNBOUNDED;
                    graphRenderer.setData(testCase.graph(), result.nodes(), isCycle);

                    long[][] matrix = FloydWarshallSolver.computeMaxMatrix(testCase.graph());
                    matrixContainer.removeAll();
                    matrixContainer.add(MatrixRenderer.build(matrix), BorderLayout.CENTER);
                    matrixContainer.revalidate();
                    matrixContainer.repaint();
                }
            }

            outputArea.setText(output.toString());
        } catch (ParseException ex) {
            outputArea.setText("Error de formato en el input:\n" + ex.getMessage());
            graphRenderer.clear();
        } catch (Mission3Orchestrator.CrossCheckMismatchException ex) {
            outputArea.setText("Mismatch detectado entre algoritmos:\n" + ex.getMessage());
        }
    }
}
