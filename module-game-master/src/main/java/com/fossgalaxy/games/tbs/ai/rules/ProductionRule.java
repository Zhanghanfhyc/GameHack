package com.fossgalaxy.games.tbs.ai.rules;


import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.List;
import java.util.Map;
import java.util.UUID;

public interface ProductionRule {

    Map<UUID, Order> perform(int playerId, GameState state, List<UUID> entities);

}
