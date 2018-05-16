package com.fossgalaxy.games.tbs.ai;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import java.util.*;

//adapted from wikipedia pseudocode
public class Pathfinder {

    public static List<CubeCoordinate> findPath(GameState s, Entity us, CubeCoordinate goal) {
        Set<CubeCoordinate> closed = new HashSet<>();
        Set<CubeCoordinate> open = new HashSet<>();

        Map<CubeCoordinate, CubeCoordinate> cameFrom = new HashMap<>();
        Map<CubeCoordinate, Double> gScore = new HashMap<>();
        Map<CubeCoordinate, Double> fScore = new HashMap<>();

        CubeCoordinate start = us.getPos();

        gScore.put(start, 0.0);
        fScore.put(start, getHeuristicCost(s, start, goal));
        open.add(start);

        while (!open.isEmpty()) {
            CubeCoordinate current = getNext(open, fScore);
            if (current.equals(goal)) {
                return rebuildPath(cameFrom, goal);
            }

            open.remove(current);
            closed.add(current);

            List<Hexagon<HexagonTile>> neighbors = calcNeighbours(s, us, current);
            for (Hexagon<HexagonTile> hex : neighbors) {
                CubeCoordinate hexPos = hex.getCubeCoordinate();
                if (closed.contains(hexPos)) {
                    continue;
                }

                if (!open.contains(hexPos)){
                    open.add(hexPos);
                }

                double tentGScore = gScore.get(current) + s.getDistance(current, hexPos);
                if (tentGScore >= gScore.getOrDefault(hexPos, Double.POSITIVE_INFINITY)) {
                    continue;
                }

                cameFrom.put(hexPos, current);
                gScore.put(hexPos, tentGScore);
                fScore.put(hexPos, tentGScore + getHeuristicCost(s, hexPos, goal));
            }
        }

        //no path exists :(
        return null;
    }

    private static List<Hexagon<HexagonTile>> calcNeighbours(GameState s, Entity us, CubeCoordinate pos){

        List<Hexagon<HexagonTile>> neighbors = new ArrayList<>(6);

        Collection<Hexagon<HexagonTile>> hc = s.getNeighbors(pos);
        for (Hexagon<HexagonTile> h : hc) {
            HexagonTile ht = h.getSatelliteData().get();
            if (ht == null) {
                continue;
            }

            Entity currOccupier = s.getEntityAt(h.getCubeCoordinate());

            //ignore enemy units - they are standing on the goal square.
            if (currOccupier != null && currOccupier.getOwner() != us.getOwner()) {
                currOccupier = null;
            }

            if (ht.isPassable(us) && currOccupier == null) {
                neighbors.add(h);
            }

        }

        return neighbors;
    }

    //would be better off using a prioirty queue...
    private static CubeCoordinate getNext(Set<CubeCoordinate> open, Map<CubeCoordinate, Double> fScores) {
        double bestCost = Double.POSITIVE_INFINITY;
        CubeCoordinate best = null;

        for (CubeCoordinate next : open) {
            Double cost = fScores.getOrDefault(next, Double.POSITIVE_INFINITY);

            if (best == null || bestCost > cost) {
                best = next;
                bestCost = cost;
            }
        }

        return best;
    }

    private static double getHeuristicCost(GameState s, CubeCoordinate from, CubeCoordinate to) {
        return s.getDistance(from, to);
    }

    private static List<CubeCoordinate> rebuildPath(Map<CubeCoordinate, CubeCoordinate> cube, CubeCoordinate goal){
        List<CubeCoordinate> path = new LinkedList<>();
        path.add(goal);

        CubeCoordinate curr = goal;
        while (cube.containsKey(curr)) {
            curr = cube.get(curr);
            path.add(curr);
        }

        Collections.reverse(path);

        return path;
    }

}
