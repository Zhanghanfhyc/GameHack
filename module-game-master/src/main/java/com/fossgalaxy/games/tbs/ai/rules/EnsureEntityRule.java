package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;

import java.util.*;

public class EnsureEntityRule implements ProductionRule {
    private final EntityType producer;
    private final EntityType produced;
    private final int threshold;

    @ObjectDef("EnsureEntity")
    public EnsureEntityRule(EntityType producer, EntityType produced, int threshold) {
        this.producer = producer;
        this.produced = produced;
        this.threshold = threshold;
    }

    @Override
    public Map<UUID, Order> perform(int playerId, GameState state, List<UUID> entities) {

        HashMap<UUID, Order> orders = new HashMap<>();

        Collection<Entity> ourEntities = state.getOwnedEntities(playerId);

        //group entities by type
        Map<EntityType, List<Entity>> entitiesByType = new HashMap<>();
        for (Entity entity : ourEntities) {
            entitiesByType.computeIfAbsent(entity.getType(), e -> new ArrayList<>()).add(entity);
        }

        //check if we need to build this type...
        int numberOfType = entitiesByType.getOrDefault(produced, Collections.emptyList()).size();
        if (numberOfType >= threshold) {
            return orders;
        }

        //check if we have any workers
        List<Entity> workers = entitiesByType.getOrDefault(producer, Collections.emptyList());
        workers.removeIf(e -> !entities.contains(e.getID()));

        if (workers.isEmpty()) {
            return orders;
        }

        //build the required amount (or less if there aren't enouph workers)
        int numberToBuild = Math.min(threshold - numberOfType, workers.size());
        for (int i=0; i<numberToBuild; i++) {
            Entity entity = workers.get(i);

            Order buildOrder = AgentUtils.buildOrder(state, entity, produced);
            if (buildOrder != null) {
                orders.put(entity.getID(), buildOrder);
                buildOrder.doOrder(entity, state);
            }

        }

        return orders;
    }

}
