package com.fossgalaxy.games.tbs.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.Map;
import java.util.stream.Collectors;

// Wins if player has x units of type
public class EntityCounter implements Rule {

    private final String winEntity;
    private final int threshold;

    @ObjectDef("EntityCounter")
    public EntityCounter(final String winEntity, final int threshold) {
        this.winEntity = winEntity;
        this.threshold = threshold;
    }

    @Override
    public Integer getWinner(final GameState state) {
        Map<Integer, Long> counts = state.getEntities().stream()
                .filter(x -> x.getType().getName().equals(winEntity))
                .collect(Collectors.groupingBy(Entity::getOwner, Collectors.counting()));
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            if (entry.getValue() >= threshold) return entry.getKey();
        }
        return NO_WINNER;
    }
}
