package com.fossgalaxy.games.tbs.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import java.util.Map;
import java.util.stream.Collectors;

public class LastManStanding implements Rule {

    @Override
    public Integer getWinner(GameState state) {
        Map<Integer, Long> counts = state.getEntities().stream()
                .collect(Collectors.groupingBy(Entity::getOwner, Collectors.counting()));
        if (counts.keySet().size() == 1) return counts.keySet().toArray(new Integer[1])[0];
        return NO_WINNER;
    }
}
