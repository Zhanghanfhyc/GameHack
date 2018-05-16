package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

public interface GameAction {


    public void renderHints(Graphics2D g, GameState s, Entity actor);

    public Order generateOrder(CubeCoordinate co, GameState s);

    /**
     * Can this entity perform this action ever?
     */
    public boolean isPossible(Entity entity, GameState state);

    /**
     * Can this entity perform this action with a given tile?
     *
     * @param entity
     * @param co
     * @return
     */
    public default boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        return isPossible(entity, s) && s.getDistance(entity.getPos(), co) <= getRange(entity);
    }

    public Color getHintColour();

    public Color getBorderColour();

    public default int getRange(Entity actor) {
        return 5;
    }

    public default boolean canAutomate() {
        return false;
    }

    public default String getCategory() {
        return null;
    }

}
