package com.fossgalaxy.games.tbs.ai.influence;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class InfluenceMap {
    private Map<CubeCoordinate, Integer> map;

    public InfluenceMap(int playerID, GameState state) {
        map = new HashMap<>();
        for (Entity entity : state.getEntities()) {
            processEntity(entity, playerID, state);
        }
    }

    public int get(CubeCoordinate target) {
        return map.getOrDefault(target, 0);
    }

    private void update(CubeCoordinate target, int value) {
        map.put(target, map.getOrDefault(target, 0) + value);
    }

    private void processEntity(Entity entity, int playerID, GameState state) {
        CubeCoordinate loc = entity.getPos();
        int meleePower = entity.getProperty("atkMelee");
        if (meleePower != 0) {
            meleePower *= (playerID == entity.getOwner()) ? 1 : -1;
            updateTiles(meleePower, state.getNeighbors(loc));
        }

        int rangedPower = entity.getProperty("atkRanged");
        if (rangedPower != 0) {
            int range = entity.getProperty("attackRange");
            rangedPower *= (playerID == entity.getOwner()) ? 1 : -1;
            updateTiles(rangedPower, state.getRange(loc, range));
        }
    }

    private void updateTiles(int power, Collection<Hexagon<HexagonTile>> tiles) {
        for (Hexagon<HexagonTile> tile : tiles) {
            update(tile.getCubeCoordinate(), power);
        }
    }
}
