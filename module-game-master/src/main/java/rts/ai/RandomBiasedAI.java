/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai;

import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.order.Order;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;
import rts.ai.core.AI;
import rts.ai.core.ParameterSpecification;
import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.ui.GameAction;
import rts.PlayerAction;
import utils.Sampler;

import java.util.*;

/**
 *
 * @author santi & webpigeon & piers
 * 
 * This AI is based on RandomBiasedAI from microRTS, shimmed for our codebase.
 * 
 */
public class RandomBiasedAI extends AI {
    static final double REGULAR_ACTION_WEIGHT = 1;
    static final double BIASED_ACTION_WEIGHT = 5;
    Random r = new Random();


    public RandomBiasedAI() {
    }
    
    
    @Override
    public void reset() {   
    }    
    
    
    @Override
    public AI clone() {
        return new RandomBiasedAI();
    }
    
    
    @Override
    public PlayerAction getAction(int player, GameState rgs) {


        PlayerAction pa = new PlayerAction();

        Collection<Entity> entities = rgs.getOwnedEntities(player);

        for (Entity entity : entities) {
            List<GameAction> legalActions = entity.getType().getAvailableActions();

            if (legalActions.size() == 1 && legalActions.get(0).canAutomate()) {
                break;
            }

            double[] distribution = new double[legalActions.size()];

            //TODO bias actions towards some things...
            int i = 0;
            for (GameAction a : legalActions) {
                distribution[i] = REGULAR_ACTION_WEIGHT;
                i++;
            }

            GameAction selected = legalActions.get(Sampler.weighted(distribution));

            CubeCoordinate targetPos = entity.getPos();

            Collection<Hexagon<HexagonTile>> htl = rgs.getNeighbors(entity.getPos());
            for (Hexagon<HexagonTile> ht : htl) {
                if (selected.isPossible(entity, rgs, ht.getCubeCoordinate())) {
                    targetPos = ht.getCubeCoordinate();
                    break;
                }
            }

            Order order = selected.generateOrder(targetPos, rgs);
            pa.addUnitAction(entity, order);

        }

        return pa;
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        return Collections.emptyList();
    }    
    
}
