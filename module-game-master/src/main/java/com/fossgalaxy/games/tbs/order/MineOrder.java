package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.ResourceType;

public class MineOrder implements Order {

    private ResourceType typeToMine;
    private int quantityPerTurn;

    public MineOrder(ResourceType typeToMine, int quantityPerTurn) {
        this.typeToMine = typeToMine;
        this.quantityPerTurn = quantityPerTurn;
    }

    @Override
    public void doOrder(Entity entity, GameState gameState) {
        Resource resource = gameState.getResourceAt(entity.getPos());
        if (resource == null) return;

        if (!typeToMine.equals(resource.getType())) return;

        int resourceMined = Math.min(resource.getAmountPerTurn(), quantityPerTurn);
        resourceMined = Math.max(0, resourceMined);

        gameState.addResource(entity.getOwner(), typeToMine, resourceMined);
    }
}
