package com.fossgalaxy.games.tbs.editor;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.io.map.MapData;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.ui.UIModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class OpenAction extends AbstractAction {
    private SettingsIO io;
    private UIModel model;

    public OpenAction(SettingsIO io, UIModel model) {
        super("open");
        this.io = io;
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        JFileChooser fc = new JFileChooser();
        int ret = fc.showOpenDialog(null);

        if (ret == JFileChooser.APPROVE_OPTION) {
        	
            MapDef def = io.loadMapDef(fc.getSelectedFile().getAbsolutePath());
            GameState state = def.buildState(model.getSettings());
            
            model.setState(state);
        }

    }
}
