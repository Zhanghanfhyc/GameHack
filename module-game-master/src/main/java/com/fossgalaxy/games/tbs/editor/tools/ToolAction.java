package com.fossgalaxy.games.tbs.editor.tools;


import com.fossgalaxy.games.tbs.editor.Editor;
import com.fossgalaxy.games.tbs.editor.EditorListener;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.util.Objects;

public class ToolAction extends AbstractAction {
    private Tool tool;
    private EditorListener listener;

    public ToolAction(EditorListener listener, Tool tool){
        super(tool.toString());

        this.tool = Objects.requireNonNull(tool);
        this.listener = Objects.requireNonNull(listener);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        listener.setTool(tool);
    }
}
