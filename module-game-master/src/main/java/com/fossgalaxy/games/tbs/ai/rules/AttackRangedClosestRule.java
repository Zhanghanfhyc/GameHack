package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Collection;

/**
 * Order a unit to perform a ranged attack on an entity that is nearest.
 *
 */
public class AttackRangedClosestRule extends PerEntityRule {

    @ObjectDef("AttackRangedMostClosest")
    public AttackRangedClosestRule(){
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return true;
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {

        CubeCoordinate ourPos = entity.getPos();

        Entity closestEnemy = null;
        double closestDist = entity.getProperty("attackRange");

        Collection<Entity> entities = state.getEntities();
        for (Entity other : entities) {
            if (other.getOwner() == entity.getOwner()) {
                continue;
            }

            CubeCoordinate otherPos = other.getPos();
            double otherDist = state.getDistance(ourPos, otherPos);

            if (otherDist <= closestDist) {
                closestEnemy = other;
                closestDist = otherDist;
            }

        }

        if (closestEnemy == null) {
            return null;
        }

        //perform ranged attack
        return AgentUtils.rangedAttack(state, entity, closestEnemy);
    }

}
