package com.fossgalaxy.games.tbs.editor;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.editor.panels.*;
import com.fossgalaxy.games.tbs.editor.tools.FloodFill;
import com.fossgalaxy.games.tbs.editor.tools.SinglePlace;
import com.fossgalaxy.games.tbs.editor.tools.ToolAction;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.ui.*;

import javax.swing.*;
import java.awt.*;
import java.io.File;

/**
 * Hello world!
 */
public class Editor {
    public static void main(String[] args) {

        String levelName = "map.json";
        String gameName = "game.json";

        if (args.length != 0 && args.length != 2) {
            System.err.println("usage: [game] [level]");
            System.exit(1);
        }

        if (args.length == 2) {
            gameName = args[0];
            levelName = args[1];
        }


        //create the initial game state
        SettingsIO io = new SettingsIO();
        run(io, gameName, levelName);
    }

    public static void run(SettingsIO io, String gameName, String levelName) {

        GameSettings settings = io.loadSettings(gameName);
        settings.finish(io);

        File levelFile = new File(levelName);
        GameState state;
        if (!levelFile.exists()) {
            state = new GameState(10, 10, settings, 60, 2);
        } else {
            MapDef mapDef = io.loadMapDef(levelName);
            state = mapDef.buildState(settings);
        }


        UIModel model = new UIModel(settings, state);

        JFrame frame = new JFrame("CE810 editor");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800,600));



        JToolBar toolBar = new JToolBar();
        toolBar.setFloatable(false);

        //misc
        toolBar.add(new NewAction(model));

        //file io
        toolBar.addSeparator();
        toolBar.add(new SaveAction(io, model));
        toolBar.add(new OpenAction(io, model));


        frame.add(toolBar, BorderLayout.NORTH);

        EditorListener listener = new EditorListener(model);

        //tools
        toolBar.addSeparator();
        toolBar.add(new ToolAction(listener, new SinglePlace()));
        toolBar.add(new ToolAction(listener, new FloodFill()));

        JSplitPane panel = new JSplitPane();
        panel.setOrientation(JSplitPane.HORIZONTAL_SPLIT);
        panel.setResizeWeight(0.25);

        JTabbedPane tabbedPane = new JTabbedPane();

        tabbedPane.addTab("resources", new ResourceSelector(settings.getResouceTypes(), listener));
        tabbedPane.addTab("terrain", new TerrainSelector(settings.getTerrainTypes(), listener));
        tabbedPane.addTab("entity", new EntityPanel(settings.getEntityTypes(), listener));

        //entity properties
        EntityProperties properties = new EntityProperties();
        model.addListener(properties);
        tabbedPane.addTab("prop", properties);

        panel.setLeftComponent(tabbedPane);


        model.addListener(listener);
        GameView view = new GameView(model);
        view.addMouseListener(new MouseController(view, model));

        JScrollPane viewport = new JScrollPane(view);
        viewport.setPreferredSize(new Dimension(800, 600));
        viewport.setBackground(Color.BLACK);

        panel.setRightComponent(viewport);

        frame.add(panel);

        frame.pack();
        frame.setVisible(true);
    }

}
