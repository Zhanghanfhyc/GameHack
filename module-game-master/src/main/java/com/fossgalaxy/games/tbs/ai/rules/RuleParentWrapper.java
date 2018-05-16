package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Only consider a rule iff a type matches.
 *
 */
public class RuleParentWrapper extends PerEntityRule {
    private final PerEntityRule rule;
    private final List<EntityType> types;

    @ObjectDef("FilterParent")
    public RuleParentWrapper(PerEntityRule rule, EntityType ... types){
        this.rule = Objects.requireNonNull(rule);
        this.types = Arrays.asList(types);
    }

    private boolean isInTree(Entity entity) {
        for (EntityType type : types){
            EntityType entityType = entity.getType();
            if (entityType.isInstance(type)) {
                return true;
            }

        }

        return false;
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return isInTree(entity) && rule.isForEntity(state, entity);
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {
        return rule.generateOrder(state, entity);
    }
}
