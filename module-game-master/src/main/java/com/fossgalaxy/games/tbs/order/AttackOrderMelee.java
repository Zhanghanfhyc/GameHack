package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import java.util.UUID;

/**
 * Created by webpigeon on 16/10/17.
 */
public class AttackOrderMelee extends AttackOrder {

    public AttackOrderMelee(UUID target) {
        super(target, "atkMelee", "defMelee");
    }

    public void doOrder(Entity entity, GameState state) {

        Entity target = state.getEntityByID(targetID);
        if (target == null) {
            return;
        }

        //melee is always distance 1.
        int range = state.getDistance(entity.getPos(), target.getPos());
        if (range > 1) {
            return;
        }

        //murdering one's self is counterproductive
        if (entity.equals(target)) {
            return;
        }


        int dmgToTarget = getDamage(entity, target);
        target.setHealth(target.getHealth() - dmgToTarget);

        int dmgToSelf = getDamage(target, entity);
        entity.setHealth(entity.getHealth() - dmgToSelf);

        //kill the entity if they run out of health.
        //this needs to be done when the order is processed, else it could effect other entities' moves.
        //eg, A murders B, C moves into B's space.
        if (target.getHealth() <= 0) {
            state.removeEntity(target);

            //add XP for killing a unit.
            entity.setProperty("xp", target.getProperty("xpAward", 0));
        }

        //we could have died to...
        if (entity.getHealth() <= 0) {
            state.removeEntity(entity);

            //add XP for killing a unit.
            if (target.getHealth() > 0) {
                target.setProperty("xp", entity.getProperty("xpAward", 0));
            }
        }
    }


}
