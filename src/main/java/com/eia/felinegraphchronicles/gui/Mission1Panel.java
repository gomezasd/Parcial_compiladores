package com.eia.felinegraphchronicles.gui;

import com.eia.felinegraphchronicles.algorithms.mission1.BfsSolver;
import com.eia.felinegraphchronicles.algorithms.mission1.DfsSolver;
import com.eia.felinegraphchronicles.io.Mission1Case;
import com.eia.felinegraphchronicles.io.Mission1Parser;
import com.eia.felinegraphchronicles.io.ParseException;
import com.eia.felinegraphchronicles.modelo.resultado.PathResult;
import com.eia.felinegraphchronicles.visualizacion.GridRenderer;

import javax.swing.*;
import java.awt.*;
import java.util.List;

public final class Mission1Panel extends JPanel {

    private static final String SAMPLE_INPUT = """
            10 10
            9
            0 1 2
            1 1 2
            2 2 2 9
            3 2 1 7
            5 3 3 6 9
            6 4 0 1 2 7
            7 3 0 3 8
            8 2 7 9
            9 3 2 3 4
            0 0
            9 9
            0 0
            """;

    private final JTextArea inputArea = new JTextArea();
    private final JTextArea outputArea = new JTextArea();
    private final GridRenderer gridRenderer = new GridRenderer();
    private final JRadioButton bfsRadio = new JRadioButton("Ver camino BFS", true);
    private final JRadioButton dfsRadio = new JRadioButton("Ver camino DFS");

    // Guardamos el resultado del primer caso resuelto, para poder
    // redibujar cuando el usuario cambia entre "ver BFS" / "ver DFS"
    private PathResult lastBfsResult;
    private PathResult lastDfsResult;

    public Mission1Panel(MainFrame mainFrame) {
        setLayout(new BorderLayout(10, 10));
        setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        add(buildHeader(mainFrame), BorderLayout.NORTH);
        add(buildBody(), BorderLayout.CENTER);

        ButtonGroup group = new ButtonGroup();
        group.add(bfsRadio);
        group.add(dfsRadio);
        bfsRadio.addActionListener(e -> updateVisualization());
        dfsRadio.addActionListener(e -> updateVisualization());
    }

    private JPanel buildHeader(MainFrame mainFrame) {
        JPanel header = new JPanel(new BorderLayout());
        JLabel title = new JLabel("Misión 1: Rescatando a Nina del campo minado (BFS & DFS)");
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

        // Lado derecho: salida de texto arriba, visualización abajo
        JPanel outputPanel = new JPanel(new BorderLayout(5, 5));
        outputArea.setFont(new Font("Monospaced", Font.PLAIN, 13));
        outputArea.setEditable(false);
        outputPanel.add(new JLabel("Resultado:"), BorderLayout.NORTH);
        outputPanel.add(new JScrollPane(outputArea), BorderLayout.CENTER);

        JPanel visualizationPanel = new JPanel(new BorderLayout(5, 5));
        JPanel radios = new JPanel(new FlowLayout(FlowLayout.LEFT));
        radios.add(bfsRadio);
        radios.add(dfsRadio);
        visualizationPanel.add(radios, BorderLayout.NORTH);
        visualizationPanel.add(gridRenderer, BorderLayout.CENTER);

        JSplitPane rightSplit = new JSplitPane(JSplitPane.VERTICAL_SPLIT, outputPanel, visualizationPanel);
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
            List<Mission1Case> cases = Mission1Parser.parse(inputArea.getText());
            StringBuilder output = new StringBuilder();

            int caseNumber = 0;
            for (Mission1Case testCase : cases) {
                caseNumber++;
                output.append(formatCase(caseNumber, testCase)).append("\n");

                // Solo guardamos el PRIMER caso para dibujarlo (dibujar
                // varios test cases a la vez no tendría sentido visual)
                if (caseNumber == 1) {
                    lastBfsResult = BfsSolver.solve(testCase.grid(), testCase.startRow(), testCase.startCol(),
                            testCase.destRow(), testCase.destCol());
                    lastDfsResult = DfsSolver.solve(testCase.grid(), testCase.startRow(), testCase.startCol(),
                            testCase.destRow(), testCase.destCol());
                    gridRenderer.setData(testCase.grid(), null);
                    updateVisualization();
                }
            }

            outputArea.setText(output.toString());
        } catch (ParseException ex) {
            outputArea.setText("Error de formato en el input:\n" + ex.getMessage());
            gridRenderer.clear();
        }
    }

    private void updateVisualization() {
        if (lastBfsResult == null) return;
        PathResult selected = bfsRadio.isSelected() ? lastBfsResult : lastDfsResult;
        List<int[]> path = selected.isReachable() ? selected.path() : List.of();
        gridRenderer.setData(gridRenderer.getGrid(), path);
    }

    private String formatCase(int caseNumber, Mission1Case testCase) {
        PathResult bfs = BfsSolver.solve(testCase.grid(), testCase.startRow(), testCase.startCol(),
                testCase.destRow(), testCase.destCol());
        if (!bfs.isReachable()) {
            return "Case #%d: Nina is unreachable".formatted(caseNumber);
        }
        PathResult dfs = DfsSolver.solve(testCase.grid(), testCase.startRow(), testCase.startCol(),
                testCase.destRow(), testCase.destCol());
        return "Case #%d: BFS %d DFS %d".formatted(caseNumber, bfs.moves(), dfs.moves());
    }
}