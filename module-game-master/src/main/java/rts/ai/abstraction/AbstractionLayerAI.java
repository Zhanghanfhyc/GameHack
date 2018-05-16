/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.abstraction;

import com.fossgalaxy.games.tbs.ai.rules.AgentUtils;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.order.AttackOrderMelee;
import com.fossgalaxy.games.tbs.order.BuildOrder;
import com.fossgalaxy.games.tbs.order.MoveOrder;
import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import rts.ai.core.AIWithComputationBudget;

import java.util.*;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import rts.*;
import rts.ai.core.ParameterSpecification;

/**
 *
 * @author santi
 */
public abstract class AbstractionLayerAI extends AIWithComputationBudget {

    // set this to true, if you believe there is a bug, and want to verify that actions
    // being generated are actually possible before sending to the game.
    public static boolean VERIFY_ACTION_CORRECTNESS = false;

    // functionality that this abstraction layer offers:
    // 1) You can forget about issuing actions to all units, just issue the ones you want, the rest are set to NONE automatically
    // 2) High level actions (using A*):
    //      - move(x,y)
    //      - train(type)
    //      - build(type,x,y)
    //      - harvest(target)
    //      - attack(target)
    protected HashMap<UUID, Order> actions = new LinkedHashMap<>();
    // In case the GameState is cloned, and the Unit pointers in the "actions" map change, this variable
    // saves a pointer to the previous GameState, if it's different than the current one, then we need to find a mapping
    // between the old units and the new ones
    protected GameState lastGameState = null;

    public AbstractionLayerAI() {
        super(-1, -1);
    }

    public AbstractionLayerAI(int timebudget, int cyclesbudget) {
        super(timebudget, cyclesbudget);
    }

    public void reset() {
        actions.clear();
    }


    /**
     * Looks like this is get the current actions per agent, as we don't have duratative actions, we don't do anything
     * here...
     *
     * @param player
     * @param gs
     * @return
     */
    public PlayerAction translateActions(int player, GameState gs) {
        return new PlayerAction(actions);
    }

    public void move(Entity u, CubeCoordinate coordinate) {
        actions.put(u.getID(), new MoveOrder(coordinate));
    }

    public void train(Entity u, EntityType unit_type, GameState gs) {
        CubeCoordinate viable = null;

        Collection<Hexagon<HexagonTile>> nextTo = gs.getNeighbors(u.getPos());
        for (Hexagon<HexagonTile> ht : nextTo) {

            if (gs.getEntityAt(ht.getCubeCoordinate()) == null) {
                viable = ht.getCubeCoordinate();
                break;
            }

        }

        if (viable == null){
            System.err.println("warning, no viable build spots...");
        }

        if (viable != null) {
            System.out.println("build order issued... "+viable+" "+unit_type);
            actions.put(u.getID(), new BuildOrder(unit_type, viable));
        }
    }

    public void build(Entity u, EntityType unit_type, CubeCoordinate pos) {
        actions.put(u.getID(), new BuildOrder(unit_type, pos));
    }

    public void harvest(Entity u, Entity target, Entity base) {
        //actions.put(u, new Harvest(u, target, base, pf));
    }

    public void moveTowards(Entity us, CubeCoordinate target, GameState gs) {
       Order action = AgentUtils.moveTowards(gs, us, target);

        if (action != null) {
            actions.put(us.getID(), action);
        }
    }

    public void moveAway(Entity us, CubeCoordinate target, GameState gs) {
        Order action = AgentUtils.moveAway(gs, us, target);
        if (action != null) {
            actions.put(us.getID(), action);
        }
    }

    public void attack(Entity us, Entity target, GameState gs) {
        double dist = gs.getDistance(us.getPos(), target.getPos());
        if (dist > 1) {
            //there will be no murder today :(
            moveTowards(us, target.getPos(), gs);
        } else {
            actions.put(us.getID(), new AttackOrderMelee(target.getID()));
        }
    }

    public void idle(Entity u) {
        actions.put(u.getID(), null);
    }

    public CubeCoordinate findBuildingPosition(List<Integer> reserved, CubeCoordinate coordinate, GameState gs) {

        CubeCoordinate cube = null;
        Collection<Hexagon<HexagonTile>> hexes = gs.getNeighbors(coordinate);

        for (Hexagon<HexagonTile> hex : hexes) {
            Entity blocker = gs.getEntityAt(hex.getCubeCoordinate());
            if (blocker == null) {
                cube = hex.getCubeCoordinate();
            }
        }

        return cube;
    }

    public boolean buildIfNotAlreadyBuilding(Entity u, EntityType type, GameState pgs) {
        Order order = getOrderFor(u);

        if (order == null) {
            CubeCoordinate buildPos = findBuildingPosition(Collections.emptyList(), u.getPos(), pgs);
            if (buildPos == null) {
                return false;
            }

            build(u, type, buildPos);
            return true;
        }

        return false;
    }

    protected Order getOrderFor(Entity u) {
        return actions.get(u);
    }

    @Override
    public String toString() {
        return getClass().getSimpleName();
    }

    @Override
    public List<ParameterSpecification> getParameters() {
        List<ParameterSpecification> parameters = new ArrayList<>();

        //parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }

}
