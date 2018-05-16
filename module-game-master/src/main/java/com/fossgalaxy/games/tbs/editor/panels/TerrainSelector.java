package com.fossgalaxy.games.tbs.editor.panels;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.editor.EditorListener;
import com.fossgalaxy.games.tbs.io.SpriteRegistry;
import com.fossgalaxy.games.tbs.parameters.TerrainType;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.Collection;

public class TerrainSelector extends JComponent {
    private SpriteRegistry sprites;

    private final int cellWidth = 64;
    private final int cellHeight = 64;

    private int cols;
    private int selected;

    private final ArrayList<TerrainType> types;


    public TerrainSelector(Collection<TerrainType> typest, EditorListener listener) {
        this.setPreferredSize(new Dimension(cellWidth * 4, cellHeight * 12));
        this.sprites = SpriteRegistry.INSTANCE;

        this.types = new ArrayList<>(typest);

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);

                    int x = e.getX() / cellWidth;
                    int y = e.getY() / cellHeight;

                    repaint();

                    selected = y * cols + x;
                    if (selected >= 0 && selected < types.size()) {
                        listener.setSelectedTerrain(types.get(selected));
                    } else {
                        listener.setSelectedTerrain(null);
                    }
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        int x = 0;
        int y = 0;

        cols = getWidth() / cellWidth;

        //too small to draw...
        if (cols == 0) {
            return;
        }

        int i=0;

        for (TerrainType type : types) {
            BufferedImage test = sprites.getImage(type.getImage());
            g.drawImage(test, x, y, cellWidth, cellHeight, Color.BLACK, null);

            i++;
            x += cellWidth;
            if (i % cols == 0) {
                x = 0;
                y += cellHeight;
            }
        }

        if (selected < types.size()) {
            g.setColor(Color.RED);
            int selectedX = (selected % cols) * cellWidth;
            int selectedY = (selected / cols) * cellHeight;
            g.drawRect(selectedX, selectedY, cellWidth, cellHeight);
        }

    }
}
