package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.parameters.GameSettings;

import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.*;

/**
 * Class storing UI related stuff.
 * <p>
 * Created by webpigeon on 16/10/17.
 */
public class UIModel {

    //upon selecting an action, if it's legal to do so self-cast it.
    public static final boolean AUTO_CAST_SELF = true;

    private static final boolean SIMULATE_MOVES = true;
    private static final boolean INTELLIGENT_END_TURN = true;

    private CubeCoordinate selected;
    private UUID currHighlight;
    private boolean showingLocations;

    private UIController controller;
    
    private GameSettings settings;
    private GameState state;

    private GameAction action;

    private Collection<GameAction> possibleActions;
    private Collection<UIModelListener> listeners;

    public UIModel(GameSettings settings, GameState state) {
    	this.settings = settings;
        this.selected = null;
        this.currHighlight = null;
        this.showingLocations = false;
        this.state = state;
        this.possibleActions = new ArrayList<>();
        this.listeners = new ArrayList<>();
    }

    public void addListener(UIModelListener listener) {
        this.listeners.add(listener);
    }

    public GameAction getAction() {
        return action;
    }

    public void setAction(GameAction action) {
        this.action = action;
    }

    public boolean isShowingLocations() {
        return this.showingLocations;
    }

    public void setShowingLocations(boolean s) {
        this.showingLocations = s;
    }

    public CubeCoordinate getSelected() {
        return selected;
    }

    public void setSelected(CubeCoordinate selected) {
        this.selected = selected;
        for (UIModelListener listener : listeners) {
            listener.onLocationSelected(selected);
        }
    }

    public Entity getCurrHighlight() {
        if (currHighlight == null) {
            return null;
        }

        return state.getEntityByID(currHighlight);
    }

    public void setHighlight(Entity highlight) {
        this.currHighlight = highlight.getID();

        Collection<Entity> movesLeft = getMovesLeft();
        if (!movesLeft.contains(highlight)) {
            action = null;
        }

        //if this unit cannot perform the selected action, set the selected action to smart mode.
        if (!highlight.getType().getAvailableActions().contains(action)) {
            this.action = null;
        }


        listeners.forEach(l -> l.onEntitySelected(highlight));
    }

    public void setController(UIController controller) {
        this.controller = controller;
    }

    public Collection<Entity> getMovesLeft() {
        if (controller == null) {
            return Collections.emptyList();
        }

        return controller.getMovesLeft();
    }

    public Map<UUID, Order> getOrderStack() {
        return controller.getOrderStack();
    }

    public boolean isSimulatingMoves() {
        return SIMULATE_MOVES;
    }

    /**
     * Add an order to our order queue and (optionally) simulate the effect.
     *
     * @param entity
     * @param order
     */
    public void addOrder(Entity entity, Order order) {
        if (controller == null) {
            //we're not really a game, just forward the state...
            order.doOrder(entity, state);
            return;
        }

        if (controller.hasOrder(entity)) {
            //that entity *HAS* an order for this turn.
            System.err.println("that's cheating - that entity already moved.");
            return;
        }

        controller.addOrder(entity, order);

        if (SIMULATE_MOVES) {
            //in civ style games, when making a move, we *expect* to see the effect of it before making another order.
            //the game deals in whole turns, not bits of a turn so we'll simulate the effect locally so the client knows
            //the state of the world after *part* of their turn.
            order.doOrder(entity, state);

            //sometimes, making a move invalidates other moves - we should stop hinting these units.
            if (INTELLIGENT_END_TURN) {
                controller.updateMovesLeft(state);
            }
        }

        this.selected = null;
        this.currHighlight = null;

        //if we've made all our moves, submit them.
        if (!controller.hasMovesLeft()) {
            done();
        }
    }

    public GameState getState() {
        return state;
    }

    public void setState(GameState state) {
        this.state = state;

        for (UIModelListener listener : listeners) {
            listener.onStateSelected(state);
        }
    }

    public void done() {
        this.action = null;
        this.selected = null;
        this.currHighlight = null;

        controller.done();
    }

    public GameAction getBestAction(CubeCoordinate target) {
        if (currHighlight == null || controller == null) {
            return null;
        }

        if (!controller.canMove(currHighlight)){
            return null;
        }

        Entity currEntity = state.getEntityByID(currHighlight);

        for (GameAction action : currEntity.getType().getAvailableActions()) {
            if (action.isPossible(state.getEntityByID(currHighlight), state, target)) {
                return action;
            }
        }

        return null;
    }

    public void addPossibleActions(Collection<GameAction> collection) {
        this.possibleActions.addAll(collection);
    }

    public boolean isOurTurn() {
        return controller.hasMovesLeft();
    }

	public GameSettings getSettings() {
		return settings;
	}

    public void notifyTurnStart() {
        for (UIModelListener listener : listeners) {
            listener.onTurnStart();
        }
    }

    public void notifyTurnEnd() {
        for (UIModelListener listener : listeners) {
            listener.onTurnEnd();
        }
    }
}
