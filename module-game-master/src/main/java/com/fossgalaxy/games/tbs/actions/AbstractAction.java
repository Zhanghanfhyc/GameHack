package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.games.tbs.ui.GameView;
import org.codetome.hexameter.core.api.Hexagon;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Collection;

/**
 * An implementation of a Hexagon Action.
 *
 * This provides reasonable hinting for action ranges, and a basic isPossible implementation.
 */
public abstract class AbstractAction implements GameAction {
    public static final Color HINT_BUILD_COLOUR = new Color(243, 163, 32, 50);
    public static final Color HINT_MINE_COLOUR = new Color(0, 0, 0, 0);
    public static final Color HINT_MOVE_COLOUR = new Color(161, 191, 253, 50);
    public static final Color HINT_ATTACK_COLOUR = new Color(229, 5, 26, 50);
    public static final Color HINT_UPGRADE_COLOUR = new Color(26, 26, 255, 0);
    public static final Color HINT_HELP_COLOUR = new Color(0, 255, 0, 50);

    private static final Stroke HINT_STROKE = new BasicStroke(2f);

    @Override
    public void renderHints(Graphics2D g, GameState s, Entity actor) {
        if (actor == null) {
            return;
        }

        Collection<Hexagon<HexagonTile>> range = s.getRange(actor.getPos(), getRange(actor));


        Area area = new Area();

        range.forEach(h -> {
            if (!isPossible(actor, s, h.getCubeCoordinate())) {
                return;
            }

            Shape hexShape = GameView.hex2shape.apply(h);
            area.add(new Area(hexShape));

            g.setColor(getHintColour());
            g.fill(hexShape);
        });

        g.setColor(getBorderColour());
        g.setStroke(HINT_STROKE);
        g.draw(area);

    }

    @Override
    public boolean isPossible(Entity entity, GameState s) {
        return entity.getType().getAvailableActions().contains(this);
    }

}
