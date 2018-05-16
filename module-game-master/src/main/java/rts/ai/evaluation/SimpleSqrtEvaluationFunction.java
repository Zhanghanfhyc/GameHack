/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.evaluation;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.object.annotations.ObjectDef;
import com.fossgalaxy.object.annotations.ObjectDefStatic;

import java.util.Collection;
import java.util.Map;

/**
 *
 * @author santi
 */
public class SimpleSqrtEvaluationFunction extends EvaluationFunction {    
    public static final float RESOURCE = 20;
    public static final float UNIT_BONUS_MULTIPLIER = 40.0f;

    private final float resourceBonus;
    private final float unitBonus;

    @ObjectDef("SqrtEval")
    public SimpleSqrtEvaluationFunction(float resourceBonus, float unitBonus) {
        this.resourceBonus = resourceBonus;
        this.unitBonus = unitBonus;
    }

    @ObjectDefStatic("SqrtEvalDefault")
    public static SimpleSqrtEvaluationFunction buildDefault(){
        return new SimpleSqrtEvaluationFunction(RESOURCE, UNIT_BONUS_MULTIPLIER);
    }

    public float evaluate(int maxplayer, int minplayer, GameState gs) {
        return base_score(maxplayer,gs) - base_score(minplayer,gs);
    }
    
    public float base_score(int player, GameState gs) {
        float score = 0;

        for(String resourceName : gs.getSettings().getResourceNames() ){
            score += gs.getResource(player, resourceName) * resourceBonus;
        }

        //
        Collection<Entity> myDudes = gs.getOwnedEntities(player);
        if (myDudes.isEmpty()) {
            return 0;
        }

        for (Entity entity : myDudes) {
            double unitCost = 0;
            EntityType t = entity.getType();
            for (int resCost : t.getCosts().values()) {
                unitCost += resCost;
            }

            score += unitCost * unitBonus * Math.sqrt(entity.getHealthFrac());
        }

        return score;
    }
    
    public float upperBound(GameState gs) {
        return 1;
    }
}
