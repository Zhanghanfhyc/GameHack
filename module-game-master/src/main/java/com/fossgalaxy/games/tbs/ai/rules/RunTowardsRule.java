package com.fossgalaxy.games.tbs.ai.rules;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Pathfinder;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Collection;
import java.util.List;

/**
 * Give entities the run away instruction
 *
 */
public class RunTowardsRule extends PerEntityRule {
    private double threshold;

    @ObjectDef("RunTowards")
    public RunTowardsRule(double threshold){
        this.threshold = threshold;
    }

    @Override
    public boolean isForEntity(GameState state, Entity entity) {
        return (entity.getHealthFrac() > threshold);
    }

    @Override
    public Order generateOrder(GameState state, Entity entity) {

        CubeCoordinate ourPos = entity.getPos();

        Entity closestEnemy = null;
        double closestDist = Double.MAX_VALUE;

        Collection<Entity> entities = state.getEntities();
        for (Entity other : entities) {
            if (other.getOwner() == entity.getOwner()) {
                continue;
            }

            CubeCoordinate otherPos = other.getPos();


            double otherDist = state.getDistance(ourPos, otherPos);

            //ignore any move that will put us on top of the target.
            if (otherDist == 1) {
                continue;
            }

            if (otherDist < closestDist) {

                //reject unreachable targets
                List<CubeCoordinate> path = Pathfinder.findPath(state, entity, otherPos);
                if (path == null) {
                    continue;
                }

                closestEnemy = other;
                closestDist = otherDist;
            }

        }

        if (closestEnemy == null) {
            return null;
        }

        return AgentUtils.pathTowards(state, entity, closestEnemy.getPos());
    }

}
