package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.UUID;

/**
 * A unit-level controller.
 * <p>
 * This controller prompts for each unit individually, then amalgamates the results into the map before returning them
 * to the game engine. This allows the development of controllers that don't care about high-level strategy but do
 * care about unit tactics.
 * <p>
 * Created by webpigeon on 22/01/18.
 */
public abstract class AbstractUnitController implements Controller {

    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        Map<UUID, Order> orderMap = new LinkedHashMap<>();

        Collection<Entity> entities = state.getOwnedEntities(playerID);
        for (Entity entity : entities) {

            Order order = getOrderFor(entity, state);
            orderMap.put(entity.getID(), order);

            order.doOrder(entity, state);
        }

        return orderMap;
    }

    protected abstract Order getOrderFor(Entity entity, GameState state);

}
