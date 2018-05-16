package com.fossgalaxy.games.tbs.editor;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.editor.tools.SinglePlace;
import com.fossgalaxy.games.tbs.editor.tools.Tool;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.ui.UIModel;
import com.fossgalaxy.games.tbs.ui.UIModelListener;
import org.codetome.hexameter.core.api.CubeCoordinate;

public class EditorListener implements UIModelListener {
    private ResourceType selectedResource;
    private TerrainType selectedTerrain;
    private PlacementType type;

    private Tool tool;

    private UIModel model;
    private EntityType selectedEntity;
    private int defaultOwner;

    public EditorListener(UIModel model){
        this.model = model;
        this.type = PlacementType.TERRAIN;
        this.tool = new SinglePlace();
        this.defaultOwner = -1;
    }

    public void setOwner(int owner){
        this.defaultOwner = owner;
    }

    public void setSelectedResource(ResourceType selectedResource) {
        this.type = PlacementType.RESOURCE;
        this.selectedResource = selectedResource;
    }

    public void setSelectedTerrain(TerrainType selectedTerrain) {
        this.type = PlacementType.TERRAIN;
        this.selectedTerrain = selectedTerrain;
    }

    public void setTool(Tool tool){
        this.tool = tool;
    }

    @Override
    public void onEntitySelected(Entity highlight) {

    }

    @Override
    public void onLocationSelected(CubeCoordinate pos) {
        GameState state = model.getState();
        if (state != null) {

            switch (type) {
                case TERRAIN:
                    tool.placeTerrain(state, pos, selectedTerrain);
                    break;
                case RESOURCE:
                    tool.placeResource(state, pos, selectedResource);
                    break;
                case ENTITY:
                    tool.placeEntity(state, pos, selectedEntity, defaultOwner);
            }

        }
    }

    public void setSelectedEntity(EntityType selectedEntity) {
        this.type = PlacementType.ENTITY;
        this.selectedEntity = selectedEntity;
    }
}
