package com.eia.felinegraphchronicles.gui;

import javax.swing.*;
import java.awt.*;

public final class MainFrame extends JFrame {

    private static final String CARD_MENU = "menu";
    private static final String CARD_MISSION1 = "mission1";
    private static final String CARD_MISSION2 = "mission2";
    private static final String CARD_MISSION3 = "mission3";
    private static final String CARD_MISSION4 = "mission4";

    private final CardLayout cardLayout = new CardLayout();
    private final JPanel cardContainer = new JPanel(cardLayout);

    public MainFrame() {
        super("The Feline Graph Chronicles");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setSize(900, 650);
        setLocationRelativeTo(null);

        cardContainer.add(buildMenuPanel(), CARD_MENU);
        cardContainer.add(new Mission1Panel(this), CARD_MISSION1);
        cardContainer.add(new Mission2Panel(this), CARD_MISSION2);
        cardContainer.add(new Mission3Panel(this), CARD_MISSION3);
        cardContainer.add(new Mission4Panel(this), CARD_MISSION4);

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

        // FIX: en la versión anterior title/subtitle se creaban pero nunca
        // se agregaban al panel — el menú se veía sin encabezado.
        panel.add(title);
        panel.add(subtitle);

        JButton mission1Button = new JButton("Misión 1: Rescatar a Nina (BFS & DFS)");
        JButton mission2Button = new JButton("Misión 2: Recuperar cuentas (Dijkstra)");
        JButton mission3Button = new JButton("Misión 3: El alijo de churun (Floyd-Warshall & Bellman-Ford)");
        JButton mission4Button = new JButton("Misión 4: Reconectar la red (Kruskal)");

        mission1Button.addActionListener(e -> showCard(CARD_MISSION1));
        mission2Button.addActionListener(e -> showCard(CARD_MISSION2));
        mission3Button.addActionListener(e -> showCard(CARD_MISSION3));
        mission4Button.addActionListener(e -> showCard(CARD_MISSION4));

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

    public void showMenu() {
        showCard(CARD_MENU);
    }
}