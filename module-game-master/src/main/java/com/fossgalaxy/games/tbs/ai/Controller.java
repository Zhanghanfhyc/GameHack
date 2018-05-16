package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.Map;
import java.util.UUID;

/**
 * Created by webpigeon on 22/01/18.
 */
public interface Controller {

    Map<UUID, Order> doTurn(int playerID, GameState state);

}
