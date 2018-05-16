package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.order.MoveOrder;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.object.annotations.ObjectDef;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

public class MoveAction extends AbstractAction {
    private static final String MOVEMENT_PROP = "movement";

    @ObjectDef("Move")
    public MoveAction(){

    }

    @Override
    public int getRange(Entity actor) {
        return actor.getType().getProperty(MOVEMENT_PROP, 0);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        //movement is not possible if the space is occupied
        Entity e = s.getEntityAt(co);
        if (e != null) {
            return false;
        }

        //movement is not possible if it's too far away
        int movementDist = entity.getType().getProperty(MOVEMENT_PROP, 0);
        if (movementDist < s.getDistance(entity.getPos(), co)) {
            return false;
        }

        TerrainType tt = s.getTerrainAt(co);
        if (tt == null || !tt.isPassible(entity)) {
            return false;
        }

        //movement might not be possible
        return isPossible(entity, s);
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        return new MoveOrder(co);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s) {
        return entity.getType().getProperty(MOVEMENT_PROP, 0) >= 1;
    }

    @Override
    public Color getHintColour() {
        return HINT_MOVE_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.BLUE;
    }

    @Override
    public String toString() {
        return "move";
    }
}
