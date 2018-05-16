package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.Parameter;

import java.util.*;

public class ProductionRuleAgent implements Controller {
    private final List<ProductionRule> rules;

    @ObjectDef("PRA")
    public ProductionRuleAgent(ProductionRule[] rules){
        this.rules = Arrays.asList(rules);
    }

    public ProductionRuleAgent() {
        this.rules = new ArrayList<>();
    }

    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        Map<UUID, Order> orders = new HashMap<>();
        List<UUID> entityIDs = new LinkedList<>();

        Collection<Entity> entities = state.getOwnedEntities(playerID);
        for (Entity entity : entities) {
            entityIDs.add(entity.getID());
        }

        for (ProductionRule rule : rules) {

            Map<UUID, Order> ruleOrders = rule.perform(playerID, state, entityIDs);
            for (Map.Entry<UUID, Order> orderEntry : ruleOrders.entrySet()) {

                entityIDs.remove(orderEntry.getKey());
                orders.putIfAbsent(orderEntry.getKey(), orderEntry.getValue());

            }

        }


        return orders;
    }
}
