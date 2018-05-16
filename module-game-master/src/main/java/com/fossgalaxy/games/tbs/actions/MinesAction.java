package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.order.MineOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

/**
 * A mine action executes an order to generate a resource. It can only be executed by an entity on a resource.
 *
 * This action can be automated - failing to provide any order to an entity that has this action will cause it to
 * execute this action at the end of the turn.
 */
public class MinesAction extends AbstractAction {

    private final ResourceType resourceType;
    private final int quantityPerTurn;

    @ObjectDef("Mine")
    public MinesAction(ResourceType resourceType, int quantityPerTurn) {
        this.resourceType = resourceType;
        this.quantityPerTurn = quantityPerTurn;
    }

    @Override
    public Order generateOrder(CubeCoordinate cubeCoordinate, GameState gameState) {
        return new MineOrder(resourceType, quantityPerTurn);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        if (!super.isPossible(entity, s, co)) return false;

        Resource resource = s.getResourceAt(co);
        if (resource == null) return false;
        if (resource.getAmountPerTurn() <= 0) return false;

        return true;
    }

    @Override
    public boolean canAutomate() {
        return true;
    }

    @Override
    public Color getHintColour() {
        return HINT_MINE_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return HINT_MINE_COLOUR;
    }

    @Override
    public String toString() {
        return "Mine: " + resourceType.getName();
    }
}
