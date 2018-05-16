package com.fossgalaxy.games.tbs.actions;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.TransferPropertyOrder;
import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.awt.*;

public class TransferPropertyAction extends AbstractAction{
    private final String property;
    private final int maxAmount;
    private final int range;

    @ObjectDef("TransferPropertyRanged")
    public TransferPropertyAction(String property, int maxAmount, int range) {
        this.property = property;
        this.maxAmount = maxAmount;
        this.range = range;
    }


    @ObjectDefStatic("TransferProperty")
    public static TransferPropertyAction rangeOne(String property, int maxAmount){
        return new TransferPropertyAction(property, maxAmount, 1);
    }

    @Override
    public boolean isPossible(Entity entity, GameState s, CubeCoordinate co) {
        if(entity.getProperty(property) < 1) return false;
        Entity other = s.getEntityAt(co);
        return other != null && s.getDistance(entity.getPos(), co) <= range;
    }

    @Override
    public Order generateOrder(CubeCoordinate co, GameState s) {
        Entity target = s.getEntityAt(co);
        return new TransferPropertyOrder(target.getID(), property, maxAmount);
    }

    @Override
    public Color getHintColour() {
        return HINT_HELP_COLOUR;
    }

    @Override
    public Color getBorderColour() {
        return Color.GREEN;
    }

    @Override
    public int getRange(Entity actor) {
        return range;
    }
}
