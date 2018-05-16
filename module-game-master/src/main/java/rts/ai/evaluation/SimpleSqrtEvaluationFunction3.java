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

import static rts.ai.evaluation.SimpleSqrtEvaluationFunction.RESOURCE;
import static rts.ai.evaluation.SimpleSqrtEvaluationFunction.UNIT_BONUS_MULTIPLIER;

/**
 *
 * @author santi
 * 
 * This function uses the same base evaluation as SimpleSqrtEvaluationFunction and SimpleSqrtEvaluationFunction2, but returns the (proportion*2)-1 of the total score on the board that belongs to one player.
 * The advantage of this function is that evaluation is bounded between -1 and 1.
 */
public class SimpleSqrtEvaluationFunction3 extends EvaluationFunction {

    private final float resourceBonus;
    private final float unitBonus;

    @ObjectDef("SqrtEval3")
    public SimpleSqrtEvaluationFunction3(float resourceBonus, float unitBonus) {
        this.resourceBonus = resourceBonus;
        this.unitBonus = unitBonus;
    }

    @ObjectDefStatic("SqrtEval3Default")
    public static SimpleSqrtEvaluationFunction3 getDefault(){
        return new SimpleSqrtEvaluationFunction3(RESOURCE, UNIT_BONUS_MULTIPLIER);
    }

    public float evaluate(int maxplayer, int minplayer, GameState gs) {
        float s1 = base_score(maxplayer,gs);
        float s2 = base_score(minplayer,gs);
        if (s1 + s2 == 0) return 0.5f;
        return  (2*s1 / (s1 + s2))-1;
    }
    
    public float base_score(int player, GameState gs) {
        float score = 0f;

        //value of resources
        //TODO cleanup resource code...
        for (String rt : gs.getSettings().getResourceNames()){
            score += gs.getResource(player, rt) * resourceBonus;
        }

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
        return 1.0f;
    }
}
