package com.fossgalaxy.games.tbs;

import com.fossgalaxy.games.tbs.ai.AIFactory;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.OrderProcessor;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.games.tbs.ui.*;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {

        if (args.length <= 2) {
            System.err.println("usage: [game] [level] [players...]");
            System.exit(1);
        }

        String gameName = args[0];
        String levelName = args[1];

        SettingsIO io = new SettingsIO();
        run(io, gameName, levelName, Arrays.copyOfRange(args, 2, args.length));
    }

    public static void run(SettingsIO io, String gameName, String levelName, String... agents) {
        boolean visual = true;


        //load game settings
        GameDef gameDef = io.loadGameDef(gameName);
        GameSettings common = io.loadSettings(gameDef);
        common.finish(io);
        AIFactory factory = new AIFactory(io, gameDef.getEvalFileName(), gameDef.getRuleFileName(), gameDef.getAiFileName());


        //load map
        MapDef map = io.loadMapDef(levelName);
        GameState level = map.buildState(common);


        // HACK TODO FIX THIS
        for (ResourceType type : common.getResouceTypes()) {
            for (int i = 0; i < level.getNumberOfPlayers(); i++) {
                level.addResource(i, type, map.getStartingResource(i, type.getName()));
            }
        }

        int numPlayers = level.getNumberOfPlayers();


        JFrame frame = new JFrame("CE810 Game Engine");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setPreferredSize(new Dimension(800, 600));

        JTabbedPane pane = new JTabbedPane();
        frame.add(pane);


        //create the players

        //TODO clone, without it all moves WILL be applied twice.
        List<GameState> playerStates = new ArrayList<>(numPlayers);
        for (int i = 0; i < numPlayers; i++) {
            playerStates.add(new GameState(level));
        }

        Controller[] controllers = new Controller[numPlayers];
        for (int i = 0; i < numPlayers; i++) {
            controllers[i] = (agents[i].equals("Player")) ? buildUI(playerStates.get(i), i, pane) : factory.buildAI(agents[i], common);
        }

        System.out.println(Arrays.deepToString(controllers));

        buildUI(level, 0, pane);


        //create the thing that's doing the turn tracking.
        OrderProcessor processor = new OrderProcessor(level, numPlayers);
        int MAX_TURNS = 10000;

        for (Rule rule : common.getVictoryConditions()) {
            processor.addRule(rule);
        }

        if (visual) {
            frame.pack();
            frame.setVisible(true);
        }

        //turn loop
        //TODO this might be better inside the turn processor?
        for (int i = 0; i < MAX_TURNS; i++) {
            processor.setupTurn();

            //figure out who's turn it is.
            int playerID = processor.getCurrentPlayer();
            Controller player = controllers[playerID];

            //process the controller's orders
            GameState curr = new GameState(level);
            Map<UUID, Order> orders = player.doTurn(playerID, curr);

            processor.doOrderBulk(orders);

            processor.finishTurn();


            if (visual) {
                //comment out if you don't want to watch...
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                frame.repaint();
            }

            if (!Objects.equals(processor.getWinner(), Rule.NO_WINNER)) {
                break;
            }
        }

        System.out.println("Game over, player won: " + processor.getWinner() + ", turns taken: " + level.getTime());
    }

    public static Controller buildUI(GameState state, int playerID, JTabbedPane pane) {
        JPanel panel = new JPanel(new BorderLayout());

        //create the UI stuff
        UIModel model = new UIModel(state.getSettings(), state);
        GameView view = new GameView(model);

        JViewport viewport = new JViewport();
        viewport.setPreferredSize(new Dimension(800, 600));
        viewport.setView(view);
        viewport.setBackground(Color.BLACK);


        DragScroller ds = new DragScroller(view);
        view.addMouseListener(ds);
        view.addMouseMotionListener(ds);
        view.addHierarchyListener(ds);

        view.addMouseListener(new MouseController(view, model));

        view.addKeyListener(new KeyboardController(model, view));
        view.setFocusable(true);
        view.requestFocus();
        panel.add(viewport);

        Box topBox = Box.createHorizontalBox();
        topBox.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        topBox.add(new ResourceBar(model, playerID));

        JButton turnDone = new JButton("end turn");

        model.addListener(new UIModelListener() {
            @Override
            public void onEntitySelected(Entity highlight) {

            }

            @Override
            public void onTurnStart() {
                turnDone.setEnabled(true);
            }

            @Override
            public void onTurnEnd() {
                turnDone.setEnabled(false);
            }
        });

        turnDone.setEnabled(false);
        turnDone.addActionListener(a -> {
            if (model.getOrderStack().isEmpty()) {
                System.out.println("You didn't do anything...");
                return;
            }

            model.done();
            view.repaint();
        });
        topBox.add(turnDone);

        panel.add(topBox, BorderLayout.NORTH);

        JToolBar actionList = new JToolBar(SwingConstants.VERTICAL);
        actionList.setFloatable(false);

        model.addListener(x -> {
            actionList.removeAll();

            Collection<Entity> movesLeft = model.getMovesLeft();
            if (model.isOurTurn() && movesLeft.contains(x)) {
                if (x != null) {
                    JButton smartBtn = new JButton("Smart");
                    smartBtn.addActionListener(a -> {
                        model.setAction(null);
                        view.repaint();
                    });

                    actionList.add(smartBtn);
                    actionList.addSeparator();

                    Map<String, JMenu> categories = new HashMap<>();

                    for (GameAction act : x.getType().getAvailableActions()) {

                        JButton actBtn = new JButton(act.toString());
                        actBtn.setToolTipText(act.toString());
                        actBtn.addActionListener(a -> {
                            if (UIModel.AUTO_CAST_SELF && act.isPossible(x, model.getState(), x.getPos())) {

                                //when selecting an action, if we can cast it on ourselves do so.

                                System.out.println("Button: " + act.toString());

                                Order currOrder = act.generateOrder(x.getPos(), state);
                                if (currOrder != null) {
                                    model.addOrder(x, currOrder);
                                }


                            } else {
                                model.setAction(act);
                            }

                            view.repaint();
                        });

                        String category = act.getCategory();
                        actionList.add(actBtn);
                    }

                    actionList.addSeparator();
                }
            }

            actionList.invalidate();
            actionList.revalidate();
            actionList.repaint();

        });

        panel.add(actionList, BorderLayout.WEST);

        pane.addTab("player " + playerID, panel);

        model.addListener(new UIModelListener() {

            @Override
            public void onLocationSelected(CubeCoordinate pos) {
                if (pos == null) {
                    return;
                }

                Hexagon<HexagonTile> h = state.cube2hex(pos);
                viewport.setViewPosition(new Point((int) h.getCenterX() - viewport.getWidth() / 2, (int) h.getCenterY() - viewport.getHeight() / 2));
            }

            @Override
            public void onEntitySelected(Entity highlight) {

            }

            public void onStateSelected(GameState s) {
                turnDone.setEnabled(true);
                viewport.repaint();
            }

        });

        return new UIController(view, model);
    }
}
