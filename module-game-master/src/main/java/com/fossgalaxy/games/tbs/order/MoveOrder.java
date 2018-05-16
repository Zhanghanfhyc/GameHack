package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.io.IOUtils;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;

/**
 * Created by webpigeon on 16/10/17.
 */
public class MoveOrder implements Order {
    private final CubeCoordinate move;

    public MoveOrder(CubeCoordinate move) {
        this.move = move;
    }

    public void doOrder(Entity entity, GameState state) {
        int range = state.getDistance(entity.getPos(), move);
        if (range > entity.getType().getProperty("movement", 0)) {
            return;
        }

        TerrainType tt = state.getTerrainAt(move);
        if (tt == null || !tt.isPassible(entity)) {
            return;
        }

        Entity entityPresent = state.getEntityAt(move);
        if (entityPresent != null) {
            return;
        }

        entity.setPos(move);
    }

    public CubeCoordinate getMove() {
        return move;
    }

    public String toString() {
        return String.format("moveTo(%s)", IOUtils.cube2String(move));
    }
}
