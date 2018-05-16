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
 * This function is similar to SimpleSqrtEvaluationFunction, except that it detects when a player has won and returns a special, larger value.
 */
public class SimpleSqrtEvaluationFunction2 extends EvaluationFunction {    

    private final float resourceBonus;
    private final float unitBonus;

    @ObjectDef("SqrtEval2")
    public SimpleSqrtEvaluationFunction2(float resourceBonus, float unitBonus) {
        this.resourceBonus = resourceBonus;
        this.unitBonus = unitBonus;
    }

    @ObjectDefStatic("SqrtEval2Default")
    public static SimpleSqrtEvaluationFunction2 getDefault(){
        return new SimpleSqrtEvaluationFunction2(RESOURCE, UNIT_BONUS_MULTIPLIER);
    }


    public float evaluate(int maxplayer, int minplayer, GameState gs) {
        float s1 = base_score(maxplayer,gs);
        float s2 = base_score(minplayer,gs);
        if (s1==0 && s2!=0) return -VICTORY;
        if (s1!=0 && s2==0) return VICTORY;
        return  s1 - s2;
    }
    
    public float base_score(int player, GameState gs) {
        float score = 0;

        for (String rt : gs.getSettings().getResourceNames()){
            score += gs.getResource(player, rt) * resourceBonus;
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
