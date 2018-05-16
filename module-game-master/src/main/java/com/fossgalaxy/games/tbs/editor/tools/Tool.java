package com.fossgalaxy.games.tbs.editor.tools;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;

public interface Tool {

    void placeTerrain(GameState state, CubeCoordinate cube, TerrainType type);
    void placeResource(GameState state, CubeCoordinate cube, ResourceType type);
    void placeEntity(GameState state, CubeCoordinate pos, EntityType selectedEntity, int owner);

}
