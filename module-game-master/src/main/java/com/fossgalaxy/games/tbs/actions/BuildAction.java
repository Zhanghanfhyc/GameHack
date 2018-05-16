package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.order.BuildOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;
import java.util.Map;

/**
 * Build action.
 *
 * Build actions declare that the target entity should construct an entity of type EntityType.
 * The build range is set at 1. This will also check that the player has the resources needed to
 * issue the construction order.
 *
 * This is intended for base and unit construction.
 *
 */
public class BuildAction extends AbstractAction {
    private static final int BUILD_RANGE = 1;

    protected EntityType type;

    @ObjectDef("Build")
    public BuildAction(EntityType type) {
        this.type = type;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new BuildOrder(type, co);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        //is the tile free?
        Entity entityAt = s.getEntityAt(co);
        if (entityAt != null) {
            return false;
        }

        TerrainType tt = s.getTerrainAt(co);
        if (tt == null || !tt.isPassible(type)){
            return false;
        }

        //can we afford it?
        for (Map.Entry<String, Integer> costEntry : type.getCosts().entrySet()) {
            int currVal = s.getResource(entity.getOwner(), costEntry.getKey());
            if (currVal < costEntry.getValue()) {
                return false;
            }
        }

        //are we next to the space?
        int distance = s.getDistance(entity.getPos(), co);
        if (distance != BUILD_RANGE) {
            return false;
        }

        return true;
    }

    @Override
    public int getRange(Entity actor) {
        return 1;
    }

    @Override
    public Color getHintColour() {
        return HINT_BUILD_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.YELLOW;
    }

    public String toString() {
        return "build " + type.getName();
    }

    public String getCategory() {
        return "build";
    }

}
