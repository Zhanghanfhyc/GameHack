package com.fossgalaxy.games.tbs.rules;

import com.fossgalaxy.games.tbs.GameState;

import java.util.Collections;
import java.util.List;

public interface Rule {
    static final Integer NO_WINNER = null;

    default Integer getWinner(GameState state) {
        return NO_WINNER;
    }

    default List<Integer> getLosers(GameState state) {
        return Collections.emptyList();
    }
}
