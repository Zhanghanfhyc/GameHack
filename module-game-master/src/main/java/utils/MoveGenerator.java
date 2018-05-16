package utils;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.ui.GameAction;
import org.codetome.hexameter.core.api.Hexagon;
import rts.PlayerAction;

import java.util.*;
import java.util.stream.Collectors;

public class MoveGenerator {
    private LinkedHashMap<UUID, List<Order>> possibleOrders;
    private int[] rotors;
    private int[] rotorSize;
    private GameState state;
    private boolean isDone = false;
    private int size;

    public MoveGenerator(GameState state, int playerID) {
        this.possibleOrders = new LinkedHashMap<>(generateAllOrders(playerID, state));
        this.rotors = new int[possibleOrders.size()];
        this.rotorSize = new int[possibleOrders.size()];
        this.state = state;


        int i=0;
        for (List<Order> order : possibleOrders.values()) {
            rotorSize[i++] = order.size();
            size += order.size();
        }
    }

    private Map<UUID, List<Order>> generateAllOrders(int playerID, GameState state) {
        return state.getOwnedEntities(playerID).stream()
                .filter(x -> !x.getType().getAvailableActions().isEmpty())
                .collect(Collectors.toMap(Entity::getID, x -> getPossibleOrdersFor(x, state)));
    }

    private List<Order> getPossibleOrdersFor(Entity entity, GameState state) {
        List<Order> entityOrders = entity.getType().getAvailableActions().stream()
                .map(action -> getPossibleOrdersFromAction(entity, action, state))
                .flatMap(List::stream)
                .collect(Collectors.toList());

        Collections.shuffle(entityOrders);
        return entityOrders;
    }

    private List<Order> getPossibleOrdersFromAction(Entity entity, GameAction action, GameState state) {
        if (action.canAutomate()) {
            return Collections.singletonList(action.generateOrder(entity.getPos(), state));
        }

        return state.getRange(entity.getPos(), action.getRange(entity)).stream()
                .map(Hexagon::getCubeCoordinate)
                .filter(location -> action.isPossible(entity, state, location))
                .map(location -> action.generateOrder(location, state))
                .collect(Collectors.toList());
    }


    public PlayerAction generateNextAction() throws Exception {

        if (isDone) {
            return null;
        }

        Map<UUID, Order> action = new HashMap<>();

        int rotor = 0;
        for(Map.Entry<UUID, List<Order>> entry : possibleOrders.entrySet()) {
            List<Order> orders = entry.getValue();
            if (orders.isEmpty()) {
                rotor++;
            } else {
                action.put(entry.getKey(), orders.get(rotors[rotor++]));
            }
        }

        //ratchet the rotors to the next entry
        for (int i=0; i<=rotors.length; ) {
            if (i == rotors.length) {
                isDone = true;
                break;
            }

            rotors[i]++;
            if (rotors[i] >= rotorSize[i]) {
                rotors[i] = 0;
                i++;
            } else {
                break;
            }

        }

        return new PlayerAction(action);
    }

    public int getSize() {
        return size;
    }

    public Map<UUID, List<Order>> getChoices() {
        return possibleOrders;
    }
}
