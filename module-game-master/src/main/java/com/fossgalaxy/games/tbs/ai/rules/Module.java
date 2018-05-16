package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.*;

public class Module implements ProductionRule{

    private final ProductionRuleAgent pra;

    @ObjectDef("Module")
    public Module(ProductionRule ... rules){
        pra = new ProductionRuleAgent(rules);
    }

    @Override
    public Map<UUID, Order> perform(int playerId, GameState state, List<UUID> entities) {
        Map<UUID, Order> orders =  pra.doTurn(playerId, state);

        entities.removeAll(orders.keySet());

        return orders;
    }
}
