package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import java.awt.*;

/**
 * Created by webpigeon on 16/10/17.
 */
public interface Order {

    void doOrder(Entity host, GameState state);

    default void render(Graphics2D g2, Entity entity) {

    }
}
