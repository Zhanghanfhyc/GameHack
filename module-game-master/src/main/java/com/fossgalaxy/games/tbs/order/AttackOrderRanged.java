package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.io.AudioRegistry;

/**
 * Ranged attacks don't deal damage to the defender.
 * <p>
 * Created by webpigeon on 16/10/17.
 */
public class AttackOrderRanged extends AttackOrder {

    public AttackOrderRanged(Entity target) {
        super(target.getID(), "atkRanged", "defRanged");
    }

    public void doOrder(Entity entity, GameState state) {
        Entity target = getTarget(state);
        if (target == null) {
            return;
        }

        int range = state.getDistance(entity.getPos(), target.getPos());
        int maxRange = entity.getType().getProperty("attackRange", 0);

        if (range > maxRange) {
            return;
        }

        //murdering one's self is counterproductive
        if (entity.equals(target)) {
            return;
        }

        int damageToTarget = getDamage(entity, target);
        target.setHealth(target.getHealth() - damageToTarget);

        //kill the entity if they run out of health.
        //this needs to be done when the order is processed, else it could effect other entities' moves.
        //eg, A murders B, C moves into B's space.
        if (target.getHealth() <= 0) {
            state.removeEntity(target);

            //add XP for killing a unit.
            entity.setProperty("xp", target.getProperty("xpAward", 0));
        }

        if (AudioRegistry.isSupported()) {
            AudioRegistry.INSTANCE.play("attack");
        }
    }

}
