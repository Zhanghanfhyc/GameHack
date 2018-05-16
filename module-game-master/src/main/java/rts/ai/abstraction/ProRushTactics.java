/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.abstraction;


import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.order.BuildOrder;
import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;
import rts.PlayerAction;
import rts.ai.core.AI;
import rts.ai.core.ParameterSpecification;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Requires rush type to be buildable from the base...
 *
 * @author santi
 */
public class ProRushTactics extends AbstractionLayerAI {
    public static final Boolean ADAVANCED_TACTICS = true;

    Random r = new Random();

    private final EntityType workerType;
    private final EntityType prodType;
    private final EntityType rushType;
    private final EntityType baseType;

    // Strategy implemented by this class:
    // If we have more than 1 "Worker": send the extra workers to attack to the nearest enemy unit
    // If we have a base: train workers non-stop
    // If we have a worker: do this if needed: build base, harvest resources
    @ObjectDef("ProRush")
    public ProRushTactics(EntityType baseType, EntityType workerType, EntityType prodType, EntityType rushType) {
        this.baseType = baseType;
        this.workerType = workerType;
        this.prodType = prodType;
        this.rushType = rushType;
    }

    @ObjectDef("ProWorkerRush")
    public ProRushTactics(EntityType baseType, EntityType workerType) {
        this(baseType, workerType, baseType, workerType);
    }

    public void reset() {
    	super.reset();
    }

    
    public AI clone() {
        return new ProRushTactics(baseType, workerType, prodType, rushType);
    }
    
    public PlayerAction getAction(int player, GameState rgs) {
        PlayerAction pa = new PlayerAction();

//        System.out.println("LightRushAI for player " + player + " (cycle " + gs.getTime() + ")");
                
        // behavior of bases:
        Collection<Entity> entities = rgs.getOwnedEntities(player);
        Map<EntityType, List<Entity>> map = entities.stream().collect(Collectors.groupingBy(Entity::getType));

        List<Entity> bases = map.getOrDefault(baseType, Collections.emptyList());
        List<Entity> workers = map.getOrDefault(workerType, Collections.emptyList());

        //don't overcommit to workers if we're not rushing them
        if (workers.size() < 2) {
            for (Entity base : bases) {
                baseBehavior(base, player, rgs);
            }
        }

        List<Entity> prods = map.getOrDefault(prodType, Collections.emptyList());
        for (Entity prod : prods) {
            prodBehavior(prod, player, rgs);
        }


        List<Entity> rushers = map.get(rushType);
        if (rushers != null) {
            for (Entity rusher : rushers) {
                meleeUnitBehavior(rusher, player, rgs);
            }
        }

        workersBehavior(workers, player, bases.size(), prods.size(), rgs);

        return translateActions(player, rgs);
    }

    private boolean canAfford(EntityType type, GameState s, int playerID) {

        Map<String, Integer> costs = type.getCosts();
        for (String costType : costs.keySet()) {
            if (s.getResource(playerID, costType) < costs.get(costType)) {
                return false;
            }
        }

        return true;
    }

    private void prodBehavior(Entity prod, int player, GameState rgs) {
        if (canAfford(rushType, rgs, player)) {
            train(prod, rushType, rgs);
        }
    }


    public void baseBehavior(Entity base, int p, GameState pgs) {
        //check removed - we don't care...
        if (canAfford(workerType, pgs, p)) {
            train(base, workerType, pgs);
        }
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

    public Entity getClosestUnfriendly(Entity us, GameState gs, double maxRange) {
        Entity closestEnemy = null;
        double closestDistance = maxRange;


        CubeCoordinate myPos = us.getPos();

        Collection<Entity> allEntities = gs.getEntities();
        for (Entity entity : allEntities) {
            if (entity.getOwner() == us.getOwner()) {
                continue;
            }

            CubeCoordinate theirPos = entity.getPos();

            double dist = gs.getDistance(myPos, theirPos);
            if (closestDistance > dist) {
                closestEnemy = entity;
                closestDistance = dist;
            }

        }

        return closestEnemy;
    }

    public void workersBehavior(List<Entity> workers, int p, int nbases, int nProd, GameState gs) {


        int resourcesUsed = 0;
        Entity harvestWorker = null;

        LinkedList<Entity> freeWorkers = new LinkedList<>();
        freeWorkers.addAll(workers);
        
        if (workers.isEmpty()) return;

        //we've pre-calculated number of bases, using that to save a little time...


        //if we have free workers and no bases, we should build a base.
        if (nbases==0 && !freeWorkers.isEmpty()) {
            Entity worker = freeWorkers.poll();
            buildIfNotAlreadyBuilding(worker, baseType, gs);
        }

        if (nProd==0 && !freeWorkers.isEmpty()) {
            Entity worker = freeWorkers.poll();
            buildIfNotAlreadyBuilding(worker, prodType, gs);
        }

        if (!ADAVANCED_TACTICS) {
            freeWorkers.forEach(e -> meleeUnitBehavior(e, p, gs));
            return;
        }

        // run away?
        Iterator<Entity> freeItr = freeWorkers.iterator();
        while (freeItr.hasNext()) {
            Entity worker = freeItr.next();

            Entity enemy = getClosestUnfriendly(worker, gs, 2);
            if (enemy != null) {
                moveAway(worker, enemy.getPos(), gs);
                freeItr.remove();
            }

            List<Resource> resources = gs.getResources();
            for (Resource resource : resources) {
                Entity building = gs.getEntityAt(resource.getLocation());
                if (building != null) {
                    continue;
                }

                if (gs.getDistance(worker.getPos(), resource.getLocation()) > 1) {
                    moveTowards(worker, resource.getLocation(), gs);
                } else if (gs.getDistance(worker.getPos(), resource.getLocation()) == 0) {
                    moveAway(worker, resource.getLocation(), gs);
                } else {
                    //find a suitable build order
                    List<GameAction> actions = worker.getType().getAvailableActions();
                    for (GameAction action : actions) {
                        if (action instanceof BuildOrder) {
                            if (action.isPossible(worker, gs, resource.getLocation())) {
                                this.actions.put(worker.getID(), action.generateOrder(resource.getLocation(), gs));
                                freeItr.remove();
                            }
                        }
                    }
                }


            }

        }


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

    }
    
    
    @Override
    public List<ParameterSpecification> getParameters()
    {
        List<ParameterSpecification> parameters = new ArrayList<>();
        
        //parameters.add(new ParameterSpecification("PathFinding", PathFinding.class, new AStarPathFinding()));

        return parameters;
    }
}
