package com.fossgalaxy.games.tbs.editor;


import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.ui.UIModel;

import javax.swing.*;
import java.awt.event.ActionEvent;

public class SaveAction extends AbstractAction {
    private UIModel state;
    private SettingsIO io;

    public SaveAction(SettingsIO io, UIModel state) {
        super("Save");
        this.io = io;
        this.state = state;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        JFileChooser fc = new JFileChooser();
        int retVal = fc.showSaveDialog(null);

        if (retVal == JFileChooser.APPROVE_OPTION) {
            Builder.saveMapData(fc.getSelectedFile(),  Builder.convertToData(state.getState()), io);
        }
    }
}
