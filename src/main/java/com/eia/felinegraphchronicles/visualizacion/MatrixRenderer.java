package com.eia.felinegraphchronicles.visualizacion;

import com.eia.felinegraphchronicles.util.Sentinels;

import javax.swing.*;
import java.awt.*;

/**
 * Construye el panel con scroll que muestra la matriz NxN de
 * Floyd-Warshall, tal como exige la Sección 6 (requisito 1) del
 * enunciado: "-" para pares sin ruta, "inf" para pares sin límite.
 */
public final class MatrixRenderer {

    private MatrixRenderer() {
    }

    public static JScrollPane build(long[][] matrix) {
        int n = matrix.length;
        String[] columnNames = new String[n + 1];
        columnNames[0] = "";
        for (int j = 0; j < n; j++) {
            columnNames[j + 1] = String.valueOf(j);
        }

        String[][] data = new String[n][n + 1];
        for (int i = 0; i < n; i++) {
            data[i][0] = String.valueOf(i);
            for (int j = 0; j < n; j++) {
                data[i][j + 1] = formatCell(matrix[i][j]);
            }
        }

        JTable table = new JTable(data, columnNames);
        table.setEnabled(false); // solo lectura
        table.setFont(new Font("Monospaced", Font.PLAIN, 12));

        JScrollPane scrollPane = new JScrollPane(table);
        scrollPane.setPreferredSize(new Dimension(450, 200));
        return scrollPane;
    }

    private static String formatCell(long value) {
        if (value == Sentinels.NO_ROUTE) return "-";
        if (value == Long.MAX_VALUE) return "inf";
        return Long.toString(value);
    }
}
