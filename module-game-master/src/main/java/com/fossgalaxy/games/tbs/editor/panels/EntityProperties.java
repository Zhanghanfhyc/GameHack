package com.fossgalaxy.games.tbs.editor.panels;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.ui.UIModel;
import com.fossgalaxy.games.tbs.ui.UIModelListener;
import org.codetome.hexameter.core.api.CubeCoordinate;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;

public class EntityProperties extends JPanel implements UIModelListener {
    private EntityTableModel tableModel;

    public EntityProperties(){
        super(new BorderLayout());
        this.tableModel = new EntityTableModel();

        JTable table = new JTable(tableModel);
        add(new JScrollPane(table));
    }

    @Override
    public void onEntitySelected(Entity highlight) {
        tableModel.setEntity(highlight);
    }

    @Override
    public void onStateSelected(GameState state) {

    }

    @Override
    public void onLocationSelected(CubeCoordinate pos) {

    }
}
