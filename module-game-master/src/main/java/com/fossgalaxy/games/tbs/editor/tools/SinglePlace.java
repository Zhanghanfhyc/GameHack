package com.fossgalaxy.games.tbs.editor.tools;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;

public class SinglePlace implements Tool {

    @Override
    public void placeTerrain(GameState state, CubeCoordinate pos, TerrainType type) {
        state.setTerrainAt(pos, type);
    }

    @Override
    public void placeResource(GameState state, CubeCoordinate pos, ResourceType type) {
        state.addResourceAt(pos, type, 1);
    }

    @Override
    public void placeEntity(GameState state, CubeCoordinate pos, EntityType selectedEntity, int owner) {
        state.addEntity(new Entity(selectedEntity, pos, owner));
    }

    public String toString(){
        return "single";
    }
}
