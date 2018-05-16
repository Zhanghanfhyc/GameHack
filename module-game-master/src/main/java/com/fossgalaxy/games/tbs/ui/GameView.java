package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.*;
import com.fossgalaxy.games.tbs.io.SpriteRegistry;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import javax.swing.*;
import java.awt.*;
import java.awt.geom.Area;
import java.awt.image.BufferedImage;
import java.util.*;
import java.util.function.Function;

/**
 * Created by webpigeon on 13/10/17.
 */
public class GameView extends JComponent {

    public static Function<Hexagon<HexagonTile>, Shape> hex2shape = hexagon -> {
        int[] x = {0, 0, 0, 0, 0, 0};
        int[] y = {0, 0, 0, 0, 0, 0};
        int n = 0;

        for (org.codetome.hexameter.core.api.Point p : hexagon.getPoints()) {
            x[n] = (int) p.getCoordinateX();
            y[n] = (int) p.getCoordinateY();
            n++;
        }

        return new Polygon(x, y, n);
    };

    private SpriteRegistry sprites;
    private UIModel model;

    public GameView(UIModel model) {
        this.setPreferredSize(new Dimension(model.getState().getGridWidthPixels(), model.getState().getGridHeightPixels()));
        this.sprites = SpriteRegistry.INSTANCE;
        this.model = model;

        model.addListener(new UIModelListener() {
            @Override
            public void onEntitySelected(Entity highlight) {
                repaint();
            }

            @Override
            public void onStateSelected(GameState state) {
                repaint();
            }
        });
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        GameState state = model.getState();

        Graphics2D g2 = (Graphics2D) g;

        // Background
        g2.setBackground(Color.BLACK);
        g2.clearRect(0, 0, getWidth(), getHeight());

        // Hexagons
        state.forAllHexagons((Hexagon<HexagonTile> hexagon) -> {
            Shape s = hex2shape.apply(hexagon);

            //draw the terrain type
            TerrainType type = hexagon.getSatelliteData().get().getTerrain();
            if (type != null) {
                BufferedImage terrainImg = sprites.getImage(type.getImage());
                Rectangle bounds = s.getBounds();
                g2.drawImage(terrainImg, (int) bounds.getX(), (int) bounds.getY(), (int) bounds.width + 1, (int) bounds.height + 1, null);
            }

            //draw the hex outline
            g2.setColor(Color.DARK_GRAY);
            g2.draw(s);

            //if we're showing locations, draw the locations.
            if (model.isShowingLocations()) {
                g2.setColor(Color.WHITE);
                String location = String.format("%dx %dy %dz", hexagon.getGridX(), hexagon.getGridY(), hexagon.getGridZ());
                g2.drawString(location, (int) hexagon.getCenterX() - 30, (int) hexagon.getCenterY());
            }
        });

        paintResources(g2);
        paintEntities(g2);

        //highlight the currently selected grid cell
        CubeCoordinate target = model.getSelected();
        if (target != null) {
            Shape hexShape = state.computeHexagonAt(target, hex2shape);
            g.setColor(Color.RED);
            g2.draw(hexShape);
        }

        //highlight the currently selected entity
        Entity highlight = model.getCurrHighlight();
        if (highlight != null) {
            Hexagon<HexagonTile> hex = state.cube2hex(highlight.getPos());

            g.setColor(Color.ORANGE);
            g.drawOval((int) hex.getCenterX() - 25, (int) hex.getCenterY() - 25, 50, 50);

        }

        GameAction action = model.getAction();
        if (action != null) {
            action.renderHints(g2, state, model.getCurrHighlight());
        } else {
            Map<GameAction, Area> zones = new HashMap<>();

            state.forAllHexagons(h -> {
                GameAction act = model.getBestAction(h.getCubeCoordinate());
                if (act != null) {
                    Area area = zones.get(act);
                    Shape shape = hex2shape.apply(h);
                    if (area == null) {
                        area = new Area(shape);
                        zones.put(act, area);
                    }
                    area.add(new Area(shape));
                }
            });

            for (Map.Entry<GameAction, Area> entry : zones.entrySet()) {
                System.out.println(entry.getKey());
                g2.setColor(entry.getKey().getHintColour());
                g2.fill(entry.getValue());

                g2.setColor(entry.getKey().getBorderColour());
                g2.draw(entry.getValue());
            }
        }

        //show orders if not simulating them
        if (!model.isSimulatingMoves()) {

            Map<UUID, Order> orders = model.getOrderStack();
            for (Map.Entry<UUID, Order> orderEntry : orders.entrySet()) {
                UUID entityID = orderEntry.getKey();

                Entity entity = state.getEntityByID(entityID);
                Order order = orderEntry.getValue();

                order.render(g2, entity);

            }
        }

    }

    /**
     * Draw all entities in the game
     *
     * @param g graphics object to draw to
     */
    protected void paintEntities(Graphics2D g) {
        //moves left
        GameState state = model.getState();
        Collection<Entity> allowedMoves = model.getMovesLeft();

        for (Entity entity : state.getEntities()) {
            Hexagon<HexagonTile> hex = state.cube2hex(entity.getPos());

            EntityType type = entity.getType();
            SpriteDef spriteDef = type.getSprite();
            if (spriteDef != null) {
                BufferedImage image = sprites.getImage(spriteDef.getImage());
                if (image != null) {
                    int halfWidth = (int) (image.getWidth() * spriteDef.getScale()) / 2;
                    int halfHeight = (int) (image.getHeight() * spriteDef.getScale()) / 2;

                    g.drawImage(
                            image,
                            (int) hex.getCenterX() - halfWidth + spriteDef.getOffset().x,
                            (int) hex.getCenterY() - halfHeight + spriteDef.getOffset().y,
                            (int) (image.getWidth() * spriteDef.getScale()),
                            (int) (image.getHeight() * spriteDef.getScale()),
                            null
                    );

                    // Health bars
                    g.setColor(Color.GREEN);
                    double healthBoxWidth = state.getHexagonWidthPixels() * 0.75;
                    double healthWidth = healthBoxWidth * entity.getHealthFrac();
                    double healthOffsetY = state.getHexagonHeightPixels() * 0.25;
                    g.fillRect(
                            (int) (hex.getCenterX() - healthBoxWidth / 2),
                            (int) (hex.getCenterY() + healthOffsetY),
                            (int) healthWidth,
                            (int) (healthBoxWidth / 10)
                    );

                    g.setColor(Color.GREEN.darker());
                    g.drawRect(
                            (int) (hex.getCenterX() - healthBoxWidth / 2),
                            (int) (hex.getCenterY() + healthOffsetY),
                            (int) healthBoxWidth,
                            (int) (healthBoxWidth / 10)
                    );
                }
            } else {
                g.setColor(Color.WHITE);
                g.fillOval((int) hex.getCenterX() - 10, (int) hex.getCenterY() - 10, 20, 20);
            }

            //if this entity has an active move, hint it.
            if (allowedMoves.contains(entity)) {
                g.setColor(Color.ORANGE);
                g.draw(hex2shape.apply(hex));
            }
        }
    }

    /**
     * Draw tile resources.
     *
     * @param g2 graphics object to draw to
     */
    protected void paintResources(Graphics2D g2) {
        GameState state = model.getState();

        for (Resource resource : state.getResources()) {

            Hexagon<HexagonTile> hex = state.cube2hex(resource.getLocation());
            Shape s = hex2shape.apply(hex);
            BufferedImage test = sprites.getImage(resource.getType().getImage());
            if (test != null) {

                //no idea what an earth the hexagons bounds are - but this is what we need.
                Rectangle bounds = s.getBounds();

                g2.drawImage(test, (int) bounds.getX(), (int) bounds.getY(), (int) bounds.width + 1, (int) bounds.height + 1, null);
            } else {
                g2.setColor(resource.getType().getColor());
                g2.fillOval((int) hex.getCenterX() - 10, (int) hex.getCenterY() - 10, 20, 20);
            }

            g2.setColor(Color.DARK_GRAY);
            g2.draw(s);

        }
    }
}
