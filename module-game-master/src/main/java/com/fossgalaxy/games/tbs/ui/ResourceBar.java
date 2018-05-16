package com.fossgalaxy.games.tbs.ui;


import com.fossgalaxy.games.tbs.GameState;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class ResourceBar extends JComponent {
    private ArrayList<String> resourceNames;

    private UIModel model;
    private int playerID;

    public ResourceBar(UIModel model, int playerID) {
        this.setPreferredSize(new Dimension(800, 20));
        this.model = model;
        this.playerID = playerID;
        this.resourceNames = new ArrayList<>(model.getState().getSettings().getResourceNames());
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState gs = model.getState();

        FontMetrics metrics = g.getFontMetrics();
        int y = metrics.getHeight();

        int startX = 0;

        for (String resource : resourceNames) {
            int val = gs.getResource(playerID, resource);

            String out = String.format("%s: %d", resource, val);
            int thisWidth = metrics.stringWidth(out);

            g.drawString(resource + ": " + val, startX, y);

            startX += thisWidth + 20;
        }

    }


}
