package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.order.GenerateOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

public class GeneratorAction extends AbstractAction {

    private final ResourceType resourceType;
    private final int quantityPerTurn;
    private final String toString;

    @ObjectDef("Generate")
    public GeneratorAction(ResourceType resourceType, int quantityPerTurn) {
        this.resourceType = resourceType;
        this.quantityPerTurn = quantityPerTurn;
        toString = String.format("Generate %d %s", quantityPerTurn, resourceType.getName());
    }

    @Override
    public boolean canAutomate() {
        return true;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new GenerateOrder(resourceType, quantityPerTurn);
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
        return toString;
    }
}
