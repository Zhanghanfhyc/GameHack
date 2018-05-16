package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.UpgradeOrder;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

/**
 * Change this entity's type from from to.
 */
public class UpgradeAction extends AbstractAction {

    private EntityType from;
    private EntityType to;

    @ObjectDef("UpgradeAction")
    public UpgradeAction(EntityType first, EntityType second) {
        this.from = first;
        this.to = second;
    }

    @Override
    public Order generateOrder(CubeCoordinate cubeCoordinate, GameState gameState) {
        return new UpgradeOrder(from, to);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        return co.equals(entity.getPos()) && isPossible(entity, s);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s) {
        return from.equals(entity.getType());
    }

    @Override
    public Color getHintColour() {
        return HINT_UPGRADE_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.CYAN;
    }

    public String toString() {
        return "upgrade " + from;
    }
}
