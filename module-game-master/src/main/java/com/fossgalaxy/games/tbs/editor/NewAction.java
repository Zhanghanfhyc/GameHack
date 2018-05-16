package com.fossgalaxy.games.tbs.editor;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.ui.UIModel;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.IOException;

public class NewAction extends AbstractAction {
    private UIModel model;

    public NewAction(UIModel model){
        super("New");
        this.model = model;
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        String name = JOptionPane.showInputDialog("map size?");
        String[] parts = name.split("[,xX]");

        if (parts.length != 2) {
            JOptionPane.showMessageDialog(null, "size should be of the format xXy", "Invalid size", JOptionPane.ERROR_MESSAGE);
            return;
        }

        try {
            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);

            GameState gs = new GameState(x, y, model.getSettings(), 60, 2);
            model.setState(gs);
        } catch (NumberFormatException ex) {
           JOptionPane.showMessageDialog(null, "Invalid number provided", "Invalid number", JOptionPane.ERROR_MESSAGE);
        }

    }

}
