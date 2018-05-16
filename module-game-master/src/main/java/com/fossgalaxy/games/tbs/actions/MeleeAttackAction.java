package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.order.AttackOrderMelee;
import com.fossgalaxy.games.tbs.order.AttackOrderRanged;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.ui.GameView;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import java.awt.*;
import java.awt.geom.Area;
import java.util.Collection;

/**
 * A ranged attack action.
 *
 * A ranged attack is an attack action that happens at a distance. The entity that is attacked doesn't get a counter
* attack (unlike melee attacks).
 */
public class MeleeAttackAction extends AbstractAction {
    private final Stroke HINT_STROKE = new BasicStroke(2f);

    @Override
    public void renderHints(Graphics2D g, GameState s, Entity actor) {
        if (actor == null) {
            return;
        }

        Collection<Hexagon<HexagonTile>> range = s.getRange(actor.getPos(), getRange(actor));

        Area area = new Area();

        range.forEach(h -> {
            Entity e = s.getEntityAt(h.getCubeCoordinate());
            if (e == null) {
                return;
            }

            //don't attack friendlies - it's rude
            if (e.getOwner() == actor.getOwner()) {
                return;
            }

            Shape hexShape = GameView.hex2shape.apply(h);

            area.add(new Area(hexShape));

            g.setColor(HINT_ATTACK_COLOUR);
            g.fill(hexShape);

            g.setColor(Color.RED);
            g.setStroke(HINT_STROKE);
            g.draw(hexShape);
        });

    }

    @Override
    public int getRange(Entity actor) {
        return 1;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        Entity e = s.getEntityAt(co);
        if (e == null) {
            return null;
        }

        return new AttackOrderMelee(e.getID());
    }


    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        //can't attack an empty spot
        Entity e = s.getEntityAt(co);
        if (e == null) {
            return false;
        }

        //don't shoot our own dudes.
        if (e.getOwner() == entity.getOwner()) {
            return false;
        }

        //attacking is not possible if it's too far away
        int movementDist = 1;
        if (movementDist < s.getDistance(entity.getPos(), co)) {
            return false;
        }

        //attacking might not be possible
        return isPossible(entity, s);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s) {
        return entity.getType().getProperty("atkMelee", 0) >= 1;
    }

    @Override
    public Color getHintColour() {
        return HINT_ATTACK_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.RED;
    }

    public String toString() {
        return "Attack";
    }
}
