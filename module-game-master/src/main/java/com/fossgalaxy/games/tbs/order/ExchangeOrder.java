package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.ResourceType;

public class ExchangeOrder implements Order {

    private final ResourceType sellType;
    private final int sellQuantity;
    private final ResourceType buyType;
    private final int buyQuantity;

    public ExchangeOrder(ResourceType sellType, int sellQuantity, ResourceType buyType, int buyQuantity) {
        this.sellType = sellType;
        this.sellQuantity = sellQuantity;
        this.buyType = buyType;
        this.buyQuantity = buyQuantity;
    }

    @Override
    public void doOrder(Entity host, GameState state) {

        if (state.getResource(host.getOwner(), sellType) > sellQuantity) {
            state.addResource(host.getOwner(), sellType, -sellQuantity);
            state.addResource(host.getOwner(), buyType, buyQuantity);
        }
    }
}
