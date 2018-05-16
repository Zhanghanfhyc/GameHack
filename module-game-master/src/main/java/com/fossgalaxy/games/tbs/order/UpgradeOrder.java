package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;

public class UpgradeOrder implements Order {
    private EntityType from;
    private EntityType to;

    public UpgradeOrder(EntityType from, EntityType to) {
        this.from = from;
        this.to = to;
    }

    @Override
    public void doOrder(Entity host, GameState state) {
        if (!from.equals(host.getType())) {
            return;
        }

        int newHealth = (int) (host.getHealthFrac() * to.getProperty("health"));

        host.setType(to);
        host.setHealth(newHealth);
    }

}
