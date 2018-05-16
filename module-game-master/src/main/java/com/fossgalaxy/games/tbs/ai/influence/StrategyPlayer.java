package com.fossgalaxy.games.tbs.ai.influence;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.Map;
import java.util.UUID;

public class StrategyPlayer implements Controller {

    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        InfluenceMap influenceMap = new InfluenceMap(playerID, state);

        return null;
    }

    private Entity findWeakestEnemy(InfluenceMap influenceMap, int playerID, GameState state){
        Entity weakest = null;
        int strength = Integer.MIN_VALUE;
        for(Entity entity : state.getEntities()){
            if(entity.getOwner() == playerID) continue;
            int entityStrength = influenceMap.get(entity.getPos());
            if(entityStrength > strength){
                weakest = entity;
                strength = entityStrength;
            }
        }
        return weakest;
    }
}
