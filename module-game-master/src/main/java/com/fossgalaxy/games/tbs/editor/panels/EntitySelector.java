package com.fossgalaxy.games.tbs.editor.panels;

import com.fossgalaxy.games.tbs.editor.EditorListener;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.entity.SpriteDef;
import com.fossgalaxy.games.tbs.io.SpriteRegistry;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

public class EntitySelector extends JComponent {
    private java.util.List<EntityType> types;
    private SpriteRegistry sprites;

    private final int cellWidth = 64;
    private final int cellHeight = 64;

    private int cols;
    private int selected;


    public EntitySelector(java.util.List<EntityType> typesa, EditorListener listener) {
        this.setPreferredSize(new Dimension(cellWidth * 4, cellHeight * 12));
        this.sprites = SpriteRegistry.INSTANCE;

        this.types = new ArrayList<>();
        for (EntityType type : typesa){
            if (type.getSprite() != null) {
                types.add(type);
            }
        }

        this.setToolTipText("Select an entity");

        this.addMouseListener(new MouseAdapter() {

            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);


                    int x = e.getX() / cellWidth;
                    int y = e.getY() / cellHeight;

                    repaint();

                    selected = y * cols + x;
                    if (selected >= 0 && selected < types.size()) {
                        listener.setSelectedEntity(types.get(selected));
                    } else {
                        listener.setSelectedEntity(null);
                    }

            }
        });
    }

    @Override
    public String getToolTipText(MouseEvent e) {

        int x = e.getX() / cellWidth;
        int y = e.getY() / cellHeight;
        int selected = y * cols + x;

        if (selected >= 0 && selected < types.size()) {
            EntityType type = types.get(selected);
            return type.toString();
        }

        return super.getToolTipText(e);
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

        for (EntityType type : types) {
            SpriteDef def = type.getSprite();
            if (def != null) {
                BufferedImage test = sprites.getImage(def.getImage());
                g.drawImage(test, x, y, cellWidth, cellHeight, Color.BLACK, null);
            }

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
