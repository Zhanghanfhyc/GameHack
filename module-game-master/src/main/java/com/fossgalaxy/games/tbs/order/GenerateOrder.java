package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.ResourceType;

public class GenerateOrder implements Order{

    private final ResourceType typeToGenerate;
    private final int quantityPerTurn;

    public GenerateOrder(ResourceType typeToGenerate, int quantityPerTurn) {
        this.typeToGenerate = typeToGenerate;
        this.quantityPerTurn = quantityPerTurn;
    }

    @Override
    public void doOrder(Entity host, GameState state) {
        state.addResource(host.getOwner(), typeToGenerate, quantityPerTurn);
    }
}
