package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;

import java.util.UUID;

public class TransferPropertyOrder implements Order{

    private final UUID target;
    private final String property;
    private final int maxAmount;

    public TransferPropertyOrder(UUID target, String property, int maxAmount) {
        this.target = target;
        this.property = property;
        this.maxAmount = maxAmount;
    }

    @Override
    public void doOrder(Entity host, GameState state) {
        if(host.getProperty(property) >= 1) {
            int amount = Math.min(maxAmount, host.getProperty(property));
            Entity other = state.getEntityByID(target);
            other.setProperty(property, other.getProperty(property) + amount);
            host.setProperty(property, host.getProperty(property) - amount);
        }
    }
}
