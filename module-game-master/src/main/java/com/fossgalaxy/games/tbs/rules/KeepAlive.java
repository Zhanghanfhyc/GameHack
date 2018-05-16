package com.fossgalaxy.games.tbs.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

// Must have at least threshold of keepThisAlive
public class KeepAlive implements Rule {

    private final String keepThisAlive;
    private final int threshold;


    @ObjectDef("KeepAlive")
    public KeepAlive(String keepThisAlive, int threshold) {
        this.keepThisAlive = keepThisAlive;
        this.threshold = threshold;
    }

    @Override
    public List<Integer> getLosers(GameState state) {
        Map<Integer, Long> counts = state.getEntities().stream()
                .filter(x -> x.getType().getName().equals(keepThisAlive))
                .collect(Collectors.groupingBy(Entity::getOwner, Collectors.counting()));

        List<Integer> results = null;
        for (Map.Entry<Integer, Long> entry : counts.entrySet()) {
            if (entry.getValue() < threshold) {
                if (results == null) results = new ArrayList<>();
                results.add(entry.getKey());
            }
        }
        return results == null ? Collections.emptyList() : results;
    }
}
