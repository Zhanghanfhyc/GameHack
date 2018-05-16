/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.abstraction;


import com.fossgalaxy.object.annotations.ObjectDef;
import rts.ai.core.AI;
import rts.ai.core.ParameterSpecification;

import java.util.*;
import java.util.stream.Collectors;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;

import rts.PlayerAction;

/**
 * Requires rush type to be buildable from the base...
 *
 * @author santi
 */
public class RushTactics extends AbstractionLayerAI {
    Random r = new Random();

    private final EntityType workerType;
    private final EntityType rushType;
    private final EntityType baseType;

    // Strategy implemented by this class:
    // If we have more than 1 "Worker": send the extra workers to attack to the nearest enemy unit
    // If we have a base: train workers non-stop
    // If we have a worker: do this if needed: build base, harvest resources
    @ObjectDef("Rush")
    public RushTactics(EntityType baseType, EntityType workerType, EntityType rushType) {
        this.workerType = workerType;
        this.rushType = rushType;
        this.baseType = baseType;
    }

    @ObjectDef("WorkerRush")
    public RushTactics(EntityType baseType, EntityType workerType) {
        this(baseType, workerType, workerType);
    }

    public void reset() {
    	super.reset();
    }

    
    public AI clone() {
        return new RushTactics(baseType, workerType, rushType);
    }
    
    public PlayerAction getAction(int player, GameState rgs) {
        PlayerAction pa = new PlayerAction();

//        System.out.println("LightRushAI for player " + player + " (cycle " + gs.getTime() + ")");
                
        // behavior of bases:
        Collection<Entity> entities = rgs.getOwnedEntities(player);
        Map<EntityType, List<Entity>> map = entities.stream().collect(Collectors.groupingBy(Entity::getType));

        List<Entity> bases = map.getOrDefault(baseType, Collections.emptyList());

        for (Entity base : bases) {
            baseBehavior(base, player, rgs);
        }


        List<Entity> rushers = map.get(rushType);
        if (rushers != null) {
            for (Entity rusher : rushers) {
                meleeUnitBehavior(rusher, player, rgs);
            }
        }

        List<Entity> workers = map.get(workerType);
        if (workers != null) {
            workersBehavior(workers, player, bases.size(), rgs);
        }
                
        return translateActions(player, rgs);
    }
    
    
    public void baseBehavior(Entity base, int p, GameState pgs) {
        //check removed - we don't care...
        train(base, workerType, pgs);
    }

    /**
     * Travel to nearest enemy if we can hit it
     *
     * @param worker
     * @param p
     * @param pgs
     */
    public void meleeUnitBehavior(Entity worker, int p, GameState pgs) {
        Entity closestEnemy = null;
        double closestDistance = Double.MAX_VALUE;


        CubeCoordinate myPos = worker.getPos();

        Collection<Entity> allEntities = pgs.getEntities();
        for (Entity entity : allEntities) {
            if (entity.getOwner() == p) {
                continue;
            }

            CubeCoordinate theirPos = entity.getPos();

            double dist = pgs.getDistance(myPos, theirPos);
            if (closestDistance > dist) {
                closestEnemy = entity;
                closestDistance = dist;
            }

        }

        if (closestEnemy != null) {
            attack(worker, closestEnemy, pgs);
        }

    }
    
    public void workersBehavior(List<Entity> workers, int p, int nbases, GameState gs) {


        int resourcesUsed = 0;
        Entity harvestWorker = null;

        LinkedList<Entity> freeWorkers = new LinkedList<>();
        freeWorkers.addAll(workers);
        
        if (workers.isEmpty()) return;

        //we've pre-calculated number of bases, using that to save a little time...


        //if we have free workers and no bases, we should build a base.
        List<Integer> reservedPositions = new LinkedList<Integer>();
        if (nbases==0 && !freeWorkers.isEmpty()) {
            Entity worker = freeWorkers.poll();
            buildIfNotAlreadyBuilding(worker, baseType, gs);
        }
        
        //if (!freeWorkers.isEmpty()) harvestWorker = freeWorkers.poll();

        //TODO handle resource gathering in a game indipendent way...

        // harvest with the harvest worker:
        /*if (harvestWorker!=null) {
            Unit closestBase = null;
            Unit closestResource = null;
            int closestDistance = 0;
            for(Unit u2:pgs.getUnits()) {
                if (u2.getType().isResource) { 
                    int d = Math.abs(u2.getX() - harvestWorker.getX()) + Math.abs(u2.getY() - harvestWorker.getY());
                    if (closestResource==null || d<closestDistance) {
                        closestResource = u2;
                        closestDistance = d;
                    }
                }
            }
            closestDistance = 0;
            for(Unit u2:pgs.getUnits()) {
                if (u2.getType().isStockpile && u2.getPlayer()==p.getID()) { 
                    int d = Math.abs(u2.getX() - harvestWorker.getX()) + Math.abs(u2.getY() - harvestWorker.getY());
                    if (closestBase==null || d<closestDistance) {
                        closestBase = u2;
                        closestDistance = d;
                    }
                }
            }
            if (closestResource!=null && closestBase!=null) {
                AbstractAction aa = getAbstractAction(harvestWorker);
                if (aa instanceof Harvest) {
                    Harvest h_aa = (Harvest)aa;
                    if (h_aa.target != closestResource || h_aa.base!=closestBase) harvest(harvestWorker, closestResource, closestBase);
                } else {
                    harvest(harvestWorker, closestResource, closestBase);
                }
            }
        }*/

        freeWorkers.forEach(e -> meleeUnitBehavior(e, p, gs));
    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        List<ParameterSpecification> parameters = new ArrayList<>();
        
        //parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }
}
