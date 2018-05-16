package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.*;
import java.util.concurrent.CountDownLatch;
import java.util.stream.Collectors;

/**
 * Player controller.
 * <p>
 * When prompted for moves, it asks the UI and blocks until all moves have been made. This prehaps isn't the most
 * elegant solution but does allow players to be treated _exactly_ the same way as AIs.
 * <p>
 * Created by webpigeon on 22/01/18.
 */
public class UIController implements Controller {

    private CountDownLatch latch;
    private List<GameAction> possibleActions;

    private Map<UUID, Order> orders;
    private Set<Entity> allowed;

    private UIModel model;
    private GameView view;

    /**
     * @param view
     * @param model
     */
    public UIController(GameView view, UIModel model) {
        this.view = view;
        this.model = model;
        this.possibleActions = new ArrayList<>();

        //keeping track of details
        this.allowed = new HashSet<>();
        this.orders = new LinkedHashMap<>(); //order of insertion is important.

        model.setController(this);
    }

    public void addPossibleActions(Collection<GameAction> collection) {
        this.possibleActions.addAll(collection);
        this.model.addPossibleActions(collection);
    }

    /**
     * Method for prompting the UI for it's move.
     * <p>
     * This should not be invoked outside of the game loop.
     *
     * @param state the current game state.
     * @return a map containing the orders to execute this turn.
     */
    @Override
    public Map<UUID, Order> doTurn(int playerID, GameState state) {
        //TODO inform the view it's time to make moves
        this.latch = new CountDownLatch(1);

        //replace the state with an up to date one.
        model.setState(state);
        model.notifyTurnStart();

        orders.clear();
        this.allowed = new HashSet<>(state.getOwnedEntities(playerID));

        if (!possibleActions.isEmpty()) {
            this.allowed = allowed.stream().filter(e -> possibleActions.stream().anyMatch(a -> a.isPossible(e, state))).collect(Collectors.toSet());
        }

        this.updateMovesLeft(state);

        view.repaint();

        try {
            //block until the UI is done with it's moves.
            latch.await();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        allowed.clear(); //make it clear we're not accepting any more moves...
        model.notifyTurnEnd();

        return orders;
    }

    /**
     * Check if an entity has been given an order this turn.
     * <p>
     * If passed an entity that cannot be controlled, or called when it's not our turn this method will return false.
     *
     * @param entity the entity to query.
     * @return true if this entity has an order for this turn, false otherwise.
     */
    public boolean hasOrder(Entity entity) {
        assert entity != null;

        return orders.containsKey(entity);
    }

    /**
     * Add an order to our queue.
     *
     * @param entity the entity to order
     * @param order  the order to issue
     */
    public void addOrder(Entity entity, Order order) {
        assert entity != null;
        assert order != null;

        if (!allowed.contains(entity)) {
            throw new IllegalArgumentException("that unit is not allowed to move");
        }

        allowed.remove(entity);
        orders.put(entity.getID(), order);
    }

    /**
     * Notify the game engine that we're done with our turn.
     * <p>
     * All entities that are under our control but have not been given an order will skip their move. Calling this when
     * it's not our turn is undefined - don't do it.
     */
    public void done() {

        //deal with people that ignored the instruction not to call this method when it's not our turn.
        if (latch == null || latch.getCount() == 0) {
            System.err.println("attempted to end the turn when it's not our turn >.<");
            return;
        }

        latch.countDown();
    }

    /**
     * Check to see if all entities are done moving.
     * <p>
     * Calling this method when it's not our turn will return false.
     *
     * @return true if at least 1 entity has not moved, false otherwise.
     */
    public boolean hasMovesLeft() {
        return !allowed.isEmpty();
    }

    /**
     * Fairly expensive method to figure out if the player can do anything at all.
     *
     * @param state
     */
    public void updateMovesLeft(GameState state) {
        Iterator<Entity> entityItr = allowed.iterator();

        while(entityItr.hasNext()) {
            Entity curr = entityItr.next();
            boolean isPossible = false;

            List<GameAction> actionList = curr.getType().getAvailableActions();
            if (actionList.size() == 1) {

                //if we only have one action and it's automatic, don't make the user click it.
                GameAction action = actionList.get(0);
                if (action.canAutomate()) {
                    entityItr.remove();
                    continue;
                }

            }

            for (GameAction action : actionList) {

                //this agent can't do anything - don't do the expensive check.
                if (!action.isPossible(curr, state)) {
                    continue;
                }

                int range = action.getRange(curr);
                Collection<Hexagon<HexagonTile>> tilesInRange = state.getRange(curr.getPos(), range);
                boolean actionPossible = tilesInRange.stream().anyMatch(h -> action.isPossible(curr, state, h.getCubeCoordinate()));
                if (actionPossible) {
                    isPossible = true;
                    break;
                }
            }

            //no action is possible - remove it
            if (!isPossible) {
                entityItr.remove();
            }
        }

        System.out.println(allowed);

    }

    public boolean canMove(UUID id) {
        for (Entity e : allowed) {
            if (id.equals(e.getID())) {
                return true;
            }
        }

        return false;
    }

    /**
     * Figure out which entities still need to move.
     * <p>
     * This returns a collection containing all entities that are able to move, but presently don't have an order
     * attached to them. If it is not out turn, the returned collection should be empty.
     * <p>
     * This collection is read-only.
     *
     * @return Collection containing entities that don't have moves.
     */
    public Collection<Entity> getMovesLeft() {
        return Collections.unmodifiableCollection(allowed);
    }

    public Map<UUID, Order> getOrderStack() {
        return Collections.unmodifiableMap(orders);
    }
}
