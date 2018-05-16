package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Map;

public class BuildOrder implements Order {
    private EntityType type;
    private CubeCoordinate cube;

    public BuildOrder(EntityType type, CubeCoordinate cube) {
        this.type = type;
        this.cube = cube;
    }

    @Override
    public void doOrder(Entity host, GameState state) {
        if (isSpaceEmpty(state)) return;
        if (!payIfPossible(host, state)) return;
        build(host, state);
    }

    protected void build(Entity host, GameState state) {
        // build it
        Entity e = new Entity(type, cube, host.getOwner());
        state.addEntity(e);
    }

    protected boolean payIfPossible(Entity host, GameState state) {
        //check we can afford to by the object
        Map<String, Integer> cost = type.getCosts();
        for (Map.Entry<String, Integer> costEntry : cost.entrySet()) {
            String t = costEntry.getKey();
            int costVal = costEntry.getValue();

            int currStore = state.getResource(host.getOwner(), t);
            if (currStore < costVal) {
                return false;
            }
        }

        // pay for it
        for (Map.Entry<String, Integer> costEntry : cost.entrySet()) {
            String t = costEntry.getKey();
            int costVal = costEntry.getValue();
            state.addResource(host.getOwner(), t, -costVal);
        }
        return true;
    }

    protected boolean isSpaceEmpty(GameState state) {
        // check the space we're building in is free
        Entity entity = state.getEntityAt(cube);
        if (entity != null) {
            return true;
        }
        return false;
    }

    public String toString() {
        return String.format("build %s at (%d, %d)", type, cube.getGridX(), cube.getGridZ());
    }

}
