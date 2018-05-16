package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * Only consider a rule iff a type matches.
 *
 */
public class RuleWrapper extends PerEntityRule {
    private final PerEntityRule rule;
    private final List<EntityType> types;

    @ObjectDef("Filter")
    public RuleWrapper(PerEntityRule rule, EntityType ... types){
        this.rule = Objects.requireNonNull(rule);
        this.types = Arrays.asList(types);
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return types.contains(entity.getType()) && rule.isForEntity(state, entity);
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {
        return rule.generateOrder(state, entity);
    }
}
