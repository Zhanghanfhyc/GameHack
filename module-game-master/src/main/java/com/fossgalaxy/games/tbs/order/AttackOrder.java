package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import java.util.UUID;

/**
 * Created by webpigeon on 22/01/18.
 */
public abstract class AttackOrder implements Order {
    protected final UUID targetID;
    protected final String attackProp;
    protected final String defProp;

    public AttackOrder(UUID target) {
        this(target, "attackDamage", "defence");
    }

    public AttackOrder(UUID target, String attackProp, String defProp) {
        this.targetID = target;
        this.attackProp = attackProp;
        this.defProp = defProp;
    }

    public AttackOrder(Entity entity) {
        this(entity.getID());
    }

    @Override
    public void doOrder(Entity host, GameState state) {

    }

    public Entity getTarget(GameState state) {
        return state.getEntityByID(targetID);
    }

    public int getDamage(Entity attacker, Entity defender) {
        int attackDamage = attacker.getType().getProperty(attackProp, 0);
        int defence = defender.getType().getProperty(defProp, 0);
        return Math.max(0, attackDamage - defence);
    }

}
