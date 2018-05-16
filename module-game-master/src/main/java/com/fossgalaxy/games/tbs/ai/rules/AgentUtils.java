package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Pathfinder;
import com.fossgalaxy.games.tbs.entity.*;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.order.*;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.Collection;
import java.util.List;
import java.util.Map;

public abstract class AgentUtils {

    /**
     * Move away from a given target location.
     *
     * @param gameState GameState to use for calculations
     * @param entity    The Entity that we are moving
     * @param target    The target location to move away from
     * @return An Order or null representing a move away from the target for the entity
     */
    public static Order moveAway(GameState gameState, Entity entity, CubeCoordinate target) {
        double distance = -Double.MAX_VALUE;
        CubeCoordinate next = null;

        Collection<Hexagon<HexagonTile>> neighbors = gameState.getNeighbors(entity.getPos());
        for (Hexagon<HexagonTile> hex : neighbors) {

            //ignore the cell if something is there...
            if (gameState.getEntityAt(hex.getCubeCoordinate()) != null) {
                continue;
            }


            double myDist = gameState.getDistance(hex.getCubeCoordinate(), target);
            if (myDist > distance) {
                next = hex.getCubeCoordinate();
                distance = myDist;
            }
        }

        if (next == null) {
            return null;
        }

        return new MoveOrder(next);
    }

    /**
     * Move towards a given target location.
     *
     * @param gameState GameState to use for calculations
     * @param entity The Entity that we are moving
     * @param target The target location ot move towards
     * @return An Order or null representing a move towards the target for the entity
     */
    public static Order pathTowards(GameState gameState, Entity entity, CubeCoordinate target) {

        List<CubeCoordinate> path = Pathfinder.findPath(gameState, entity, target);
        if (path == null) {
            //can't do it, no path...
            System.err.println("no legal path found for "+entity+" to "+target.getGridX()+" "+target.getGridZ());
            return null;
        }

        return new MoveOrder(path.get(1));
    }

    public static Order moveTowards(GameState gameState, Entity entity, CubeCoordinate target){

        double distance = Double.MAX_VALUE;
        CubeCoordinate next = null;

        Collection<Hexagon<HexagonTile>> neighbors = gameState.getNeighbors(entity.getPos());
        for (Hexagon<HexagonTile> hex : neighbors) {

            //ignore the cell if something is there...
            if (gameState.getEntityAt(hex.getCubeCoordinate()) != null) {
                continue;
            }

            HexagonTile gt = hex.getSatelliteData().get();
            if (!gt.isPassable(entity)){
                continue;
            }

            double myDist = gameState.getDistance(hex.getCubeCoordinate(), target);
            if (myDist < distance) {
                next = hex.getCubeCoordinate();
                distance = myDist;
            }
        }

        if (next == null) {
            return null;
        }

        return new MoveOrder(next);
    }


    public static Order rangedAttack(GameState gameState, Entity entity, Entity target) {
        double closestDist = gameState.getDistance(entity.getPos(), target.getPos());

        //check the closest enemy is within range.
        double attackRange = entity.getType().getProperty("attackRange", 0);
        if (closestDist > attackRange) {
            return null;
        }

        return new AttackOrderRanged(target);
    }

    public static boolean canAfford(int playerID, GameState gameState, EntityType buildingType) {
        Map<String, Integer> costs = buildingType.getCosts();

        for (Map.Entry<String, Integer> costEntry : costs.entrySet()) {
            int currAmount = gameState.getResource(playerID, costEntry.getKey());
            if (currAmount < costEntry.getValue()) {
                return false;
            }
        }

        return true;
    }

    public static Order buildOrder(GameState gameState, Entity entity, EntityType buildingType) {
        if (!canAfford(entity.getOwner(), gameState, buildingType)) {
            return null;
        }

        CubeCoordinate buildPos = findBuildingPosition(gameState, buildingType, entity.getPos());
        if (buildPos == null) {
            return null;
        }

        return new BuildOrder(buildingType, buildPos);
    }

    public static CubeCoordinate findBuildingPosition(GameState gameState, EntityType type, CubeCoordinate coordinate) {

        CubeCoordinate cube = null;
        Collection<Hexagon<HexagonTile>> hexes = gameState.getNeighbors(coordinate);

        for (Hexagon<HexagonTile> hex : hexes) {

            TerrainType tt = hex.getSatelliteData().get().getTerrain();
            if (tt == null || !tt.isPassible(type)) {
                continue;
            }

            Entity blocker = gameState.getEntityAt(hex.getCubeCoordinate());
            if (blocker == null) {
                cube = hex.getCubeCoordinate();
            }
        }

        return cube;
    }

    public static Order MeleeAttack(GameState gameState, Entity entity, Entity target) {
        double dist = gameState.getDistance(entity.getPos(), target.getPos());
        if (dist > 1) {
            //there will be no murder today :(
            return pathTowards(gameState, entity, target.getPos());
        } else {
            return new AttackOrderMelee(target.getID());
        }
    }
}
