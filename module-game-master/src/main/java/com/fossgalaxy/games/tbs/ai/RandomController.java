package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.ui.GameAction;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * As it says on the tin - returns a random order per unit.
 * <p>
 * But will not allow that unit to stay still ...
 * <p>
 * Could iterate on that
 * <p>
 * Orders with big range will dominate
 * <p>
 * Items that can only spend will spend all the money
 * <p>
 * Terrible controller really
 */
public class RandomController implements Controller {

    private final Random random = new Random();

    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        return state.getOwnedEntities(playerID).stream()
                .filter(x -> x.getType().getAvailableActions().isEmpty())
                .collect(Collectors.toMap(Entity::getID, x -> chooseRandomOrder(x, state)));
    }

    // Getting random orders is tricky - Which coordinates can be used?
    private Order chooseRandomOrder(Entity entity, GameState state) {
        List<Order> possible = entity.getType().getAvailableActions().stream()
                .map(action -> getPossibleOrdersFromAction(entity, action, state))
                .flatMap(List::stream)
                .collect(Collectors.toList());
        if (possible.isEmpty()) return null;
        return possible.get(random.nextInt(possible.size()));
    }

    private List<Order> getPossibleOrdersFromAction(Entity entity, GameAction action, GameState state) {
        return state.getRange(entity.getPos(), action.getRange(entity)).stream()
                .map(Hexagon::getCubeCoordinate)
                .filter(location -> action.isPossible(entity, state, location))
                .map(location -> action.generateOrder(location, state))
                .collect(Collectors.toList());
    }
}
