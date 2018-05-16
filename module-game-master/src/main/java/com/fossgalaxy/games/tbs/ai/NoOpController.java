package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.Collections;
import java.util.Map;
import java.util.UUID;

/**
 * Created by webpigeon on 22/01/18.
 */
public class NoOpController implements Controller {

    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        return Collections.emptyMap();
    }

}
