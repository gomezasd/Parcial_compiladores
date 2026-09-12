package com.eia.felinegraphchronicles.gui;

import javax.swing.*;
import java.awt.*;

/**
 * Ventana principal: muestra un menú para elegir cuál de las 4 misiones
 * resolver, y cambia entre paneles usando CardLayout (así no se abren
 * ventanas nuevas, todo pasa dentro de la misma ventana).
 */
public final class MainFrame extends JFrame {

    // Nombres identificadores de cada "carta" dentro del CardLayout
    private static final String CARD_MENU = "menu";
    private static final String CARD_MISSION1 = "mission1";
    private static final String CARD_MISSION3 = "mission3";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    public MainFrame() {
        super("The Feline Graph Chronicles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null); // centra la ventana en la pantalla

        cardContainer.add(buildMenuPanel(), CARD_MENU);

        // Cada panel recibe una referencia a "este" MainFrame para poder
        // pedirle que vuelva al menú (ver showMenu() más abajo)
        cardContainer.add(new Mission1Panel(this), CARD_MISSION1);
        cardContainer.add(new Mission3Panel(this), CARD_MISSION3);

        add(cardContainer);
        showMenu();
    }

    private JPanel buildMenuPanel() {
        JPanel panel = new JPanel();
        panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
        panel.setBorder(BorderFactory.createEmptyBorder(60, 100, 60, 100));

        JLabel title = new JLabel("The Feline Graph Chronicles");
        title.setFont(new Font("SansSerif", Font.BOLD, 26));
        title.setAlignmentX(Component.CENTER_ALIGNMENT);

        JLabel subtitle = new JLabel("Elige una misión para ayudar a Pola y Minerva:");
        subtitle.setAlignmentX(Component.CENTER_ALIGNMENT);
        subtitle.setBorder(BorderFactory.createEmptyBorder(10, 0, 30, 0));

        JButton mission1Button = new JButton("Misión 1: Rescatar a Nina (BFS & DFS)");
        JButton mission2Button = new JButton("Misión 2: Recuperar cuentas (Dijkstra)");
        JButton mission3Button = new JButton("Misión 3: El alijo de churun (Floyd-Warshall & Bellman-Ford)");
        JButton mission4Button = new JButton("Misión 4: Reconectar la red (Kruskal)");

        mission1Button.addActionListener(e -> showCard(CARD_MISSION1));
        mission3Button.addActionListener(e -> showCard(CARD_MISSION3));

        // Misiones 2 y 4 las va a construir tu compañero — por ahora
        // quedan deshabilitadas para que el menú no rompa si las tocan
        mission2Button.setEnabled(false);
        mission4Button.setEnabled(false);

        for (JButton button : new JButton[]{mission1Button, mission2Button, mission3Button, mission4Button}) {
            button.setAlignmentX(Component.CENTER_ALIGNMENT);
            button.setMaximumSize(new Dimension(500, 40));
            panel.add(button);
            panel.add(Box.createVerticalStrut(15));
        }

        return panel;
    }

    private void showCard(String cardName) {
        cardLayout.show(cardContainer, cardName);
    }

    /** Los paneles de misión llaman esto para volver al menú principal. */
    public void showMenu() {
        showCard(CARD_MENU);
    }
}
