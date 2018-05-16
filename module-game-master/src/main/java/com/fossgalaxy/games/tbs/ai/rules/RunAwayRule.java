package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.*;

/**
 * Give entities the run away instruction
 *
 */
public class RunAwayRule extends PerEntityRule {
    private double threshold;


    @ObjectDef("RunAway")
    public RunAwayRule(double threshold){
        this.threshold = threshold;
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return (entity.getHealthFrac() <= threshold);
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {

        CubeCoordinate ourPos = entity.getPos();

        Entity closestEnemy = null;
        double closestDist = Double.MAX_VALUE;

        Collection<Entity> entities = state.getEntities();
        for (Entity other : entities) {
            if (other.getOwner() == entity.getOwner()) {
                continue;
            }

            CubeCoordinate otherPos = other.getPos();
            double otherDist = state.getDistance(ourPos, otherPos);

            if (otherDist < closestDist) {
                closestEnemy = other;
                closestDist = otherDist;
            }

        }

        if (closestEnemy == null) {
            return null;
        }

        return AgentUtils.moveAway(state, entity, closestEnemy.getPos());
    }

}
