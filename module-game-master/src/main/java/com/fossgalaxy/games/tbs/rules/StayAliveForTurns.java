package com.fossgalaxy.games.tbs.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.object.annotations.ObjectDef;

public class StayAliveForTurns implements Rule {
    private final Integer player;
    private final int threshold;
    private int turnCount = 0;


    @ObjectDef("StayAliveForTurns")
    public StayAliveForTurns(Integer player, int threshold) {
        this.player = player;
        this.threshold = threshold;
    }

    @Override
    public Integer getWinner(GameState state) {
        // Gamestate does not know how old it is
        return NO_WINNER;
    }
}
