package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Collection;

/**
 * Order an entity to perform a ranged attack on the lowest health fraction unit within range.
 *
 */
public class AttackRangedMostDamangedRule extends PerEntityRule {

    @ObjectDef("AttackRangedMostDamaged")
    public AttackRangedMostDamangedRule(){
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return true;
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {

        CubeCoordinate ourPos = entity.getPos();

        double range = entity.getType().getProperty("attackRange", 0);

        Entity mostDamagedEntity = null;
        double mostDamagedVal = 1;

        Collection<Entity> entities = state.getEntities();
        for (Entity other : entities) {
            if (other.getOwner() == entity.getOwner()) {
                continue;
            }

            CubeCoordinate otherPos = other.getPos();
            double otherRange = state.getDistance(ourPos, otherPos);
            double otherVal = other.getHealthFrac();

            if (otherRange <= range && otherVal < mostDamagedVal) {
                mostDamagedEntity = other;
                mostDamagedVal = otherVal;
            }

        }

        if (mostDamagedEntity == null) {
            return null;
        }

        //perform ranged attack
        return AgentUtils.rangedAttack(state, entity, mostDamagedEntity);
    }

}
