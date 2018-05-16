package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

/**
 * Build on Resource.
 *
 * This is an extention of the Build Action that only permits building on a resource type.
 * This action should be used for resource production buildings that need to be built on a resource.
 */
public class BuildOnResourceAction extends BuildAction {

    private ResourceType typeOn;

    // Can build type only on places containing typeOn
    @ObjectDef("BuildOnResource")
    public BuildOnResourceAction(EntityType type, ResourceType typeOn) {
        super(type);
        this.typeOn = typeOn;
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        if (!super.isPossible(entity, s, co)) return false;

        // Check resource of correct type is present
        Resource resourceAt = s.getResourceAt(co);
        if (resourceAt == null) {
            return false;
        }

        return (resourceAt.getType().equals(typeOn));
    }
}
