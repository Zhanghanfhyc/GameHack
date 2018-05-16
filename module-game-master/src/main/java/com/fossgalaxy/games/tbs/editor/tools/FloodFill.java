package com.fossgalaxy.games.tbs.editor.tools;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.Collection;
import java.util.LinkedList;
import java.util.Queue;

public class FloodFill implements Tool {

    @Override
    public void placeTerrain(GameState state, CubeCoordinate cube, TerrainType replacement) {

        TerrainType targetType = state.getTerrainAt(cube);
        Queue<CubeCoordinate> queue = new LinkedList<>();

        if ( (replacement == null && targetType == null) || (replacement != null && replacement.equals(targetType)) ) {
            return;
        }

        state.setTerrainAt(cube, replacement);
        queue.add(cube);

        while (!queue.isEmpty()) {
            CubeCoordinate n = queue.poll();

            Collection<Hexagon<HexagonTile>> neighbors = state.getNeighbors(n);
            for (Hexagon<HexagonTile> hex : neighbors) {
                if (hex.getSatelliteData().isPresent()) {
                    HexagonTile tile = hex.getSatelliteData().get();

                    if (tile.hasTerrain(targetType)) {
                        tile.setTerrain(replacement);
                        queue.add(hex.getCubeCoordinate());
                    }
                }
            }

        }

    }

    @Override
    public void placeResource(GameState state, CubeCoordinate cube, ResourceType type) {

    }

    @Override
    public void placeEntity(GameState state, CubeCoordinate pos, EntityType selectedEntity, int owner) {

    }

    @Override
    public String toString() {
        return "Fill";
    }
}
