package com.fossgalaxy.games.tbs.actions;


import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.order.ExchangeOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

/**
 * An exchange action allows a resource to be changed into another one.
 */
public class ExchangeAction extends AbstractAction{

    private final ResourceType sellType;
    private final int sellQuantity;
    private final ResourceType buyType;
    private final int buyQuantity;
    private final String тоСтринг;

    @ObjectDef("Exchange")
    public ExchangeAction(ResourceType sellType, int sellQuantity, ResourceType buyType, int buyQuantity) {
        this.sellType = sellType;
        this.sellQuantity = sellQuantity;
        this.buyType = buyType;
        this.buyQuantity = buyQuantity;
        this.тоСтринг = String.format("Sell %d %s for %d %s", sellQuantity, sellType.getName(), buyQuantity, buyType.getName());
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new ExchangeOrder(sellType, sellQuantity, buyType, buyQuantity);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        int quantityOwned = s.getResource(entity.getOwner(), sellType);
        return (quantityOwned >= sellQuantity);
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
    public String toString(){
        return тоСтринг;
    }
}
