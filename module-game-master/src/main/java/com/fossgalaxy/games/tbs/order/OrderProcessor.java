package com.fossgalaxy.games.tbs.order;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.games.tbs.ui.GameAction;

import java.util.*;

/**
 * Created by webpigeon on 22/01/18.
 */
public class OrderProcessor {

    private int turnNumber;
    private int currPlayer;
    private int numPlayers;
    private Integer winner;
    private Set<Integer> losers;

    private Set<Entity> entitiesNeedingOrders;
    private GameState state;

    private List<Rule> rules;

    public OrderProcessor(GameState state, int numPlayers) {
        this.entitiesNeedingOrders = new HashSet<>();
        this.rules = new ArrayList<>();
        this.losers = new HashSet<Integer>();
        this.state = state;
        this.turnNumber = 0;
        this.currPlayer = 0;
        this.numPlayers = numPlayers;
    }

    public void setupTurn() {
        entitiesNeedingOrders.addAll(state.getOwnedEntities(currPlayer));

        //any environmental/global stuff "at start of turn"
    }

    public void doOrder(UUID entityID, Order order) {
        Entity entity = state.getEntityByID(entityID);

        //ignore illegal unit orders...
        if (entity == null) {
            return;
        }

        if (!entitiesNeedingOrders.contains(entity)) {
            throw new IllegalArgumentException("not that entity's turn!");
        }

        if (entity.getHealth() <= 0) {
            System.err.println("entity is dead, won't move");
            // entity was dead when order issued
            // could happen accidentally if the unit died during it's turn or the action didn't reap properly.
            return;
        }

        order.doOrder(entity, state);
    }

    public void doAutomaticActions() {
        for (Entity entity : entitiesNeedingOrders) {

            for (GameAction act : entity.getType().getAvailableActions()) {

                if (act.canAutomate()) {
                    Order order = act.generateOrder(entity.getPos(), state);
                    order.doOrder(entity, state);
                    break;
                }

            }

        }

    }

    public void skipAllRemaining() {
        entitiesNeedingOrders.clear();
    }

    /**
     * Tell the game that it's the next player's turn.
     * <p>
     * Any unit not issued with an order will be skipped.
     */
    public void finishTurn() {
        if (!isTurnOver()) {
            skipAllRemaining();
        }

        //any environmental/global stuff "at end of turn".
        for (Rule rule : rules) {
            Integer ruleWinner = rule.getWinner(state);
            if (ruleWinner != Rule.NO_WINNER) {
                winner = ruleWinner;
                break;
            }
            List<Integer> ruleLosers = rule.getLosers(state);
            if (!ruleLosers.isEmpty()) {
                losers.addAll(ruleLosers);
            }
        }

        //update the turn counter skipping losers
        state.tick();

        turnNumber++;
        while (true) {
            currPlayer = (currPlayer + 1) % numPlayers;
            if (!losers.contains(currPlayer)) break;
        }
    }

    public Integer getWinner() {
        return winner;
    }

    public Collection<Entity> getEntitiesNeedingOrders() {
        return Collections.unmodifiableCollection(entitiesNeedingOrders);
    }

    public boolean isTurnOver() {
        return entitiesNeedingOrders.isEmpty();
    }

    public void doOrderBulk(Map<UUID, Order> orders) {

        for (Map.Entry<UUID, Order> orderEntry : orders.entrySet()) {
            doOrder(orderEntry.getKey(), orderEntry.getValue());
        }
        doAutomaticActions();
        skipAllRemaining();
    }

    public int getTurnNumber() {
        return turnNumber;
    }

    public int getCurrentPlayer() {
        return currPlayer;
    }

    public void addRule(Rule rule) {
        rules.add(rule);
    }
}
