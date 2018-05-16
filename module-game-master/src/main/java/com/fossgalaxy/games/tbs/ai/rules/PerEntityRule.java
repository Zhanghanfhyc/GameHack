package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public abstract class PerEntityRule implements ProductionRule {

    @Override
    public Map<UUID, Order> perform(int playerId, GameState state, List<UUID> entities) {

        Map<UUID, Order> orders = new HashMap<>();

        for (UUID uid : entities) {
            Entity entity = state.getEntityByID(uid);

            if (isForEntity(state, entity)) {
                Order generated = generateOrder(state, entity);
                if (generated != null) {

                    orders.put(uid, generated);
                    generated.doOrder(entity, state);

                }
            }

        }

        return orders;
    }

    public abstract boolean isForEntity(GameState state, Entity entity);

    public abstract Order generateOrder(GameState state, Entity entity);
}
