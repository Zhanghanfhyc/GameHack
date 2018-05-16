package rts;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.ui.GameAction;

import java.util.*;

public class PlayerAction {
    private final Map<UUID, Order> order;

    public PlayerAction(){
        this.order = new HashMap<>();
    }

    public PlayerAction(Map<UUID, Order> order){
        this.order = order;
    }

    public void fillWithNones(GameState gs, int player, int i) {

    }

    public Map<UUID, Order> getOrders() {
        return order;
    }

    public void addUnitAction(Entity entity, Order selected) {
        order.put(entity.getID(), selected);
    }

    public void addUnitAction(UUID entity, Order selected) {
        order.put(entity, selected);
    }

    public void apply(GameState gs) {

        for (Map.Entry<UUID, Order> entry : order.entrySet()) {
            Entity who = gs.getEntityByID(entry.getKey());
            Order order = entry.getValue();
            order.doOrder(who, gs);
        }

        gs.tick();
    }

    public PlayerAction merge(PlayerAction restOfAction) {
        return this;
    }

    public static List<PlayerAction> GenerateForEntity(Entity entity, GameState gs) {
        return Collections.emptyList();
    }

    public String toString() {
        return order.toString();
    }

}
