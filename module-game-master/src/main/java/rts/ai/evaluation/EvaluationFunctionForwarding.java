/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.evaluation;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.object.annotations.ObjectDef;

/**
 *
 * @author santi
 */
public class EvaluationFunctionForwarding extends EvaluationFunction {
    
    EvaluationFunction baseFunction = null;

    @ObjectDef("ForwardEval")
    public EvaluationFunctionForwarding(EvaluationFunction base) {
        baseFunction = base;
    }
    
    
    public float evaluate(int maxplayer, int minplayer, GameState gs) {
        GameState gs2 = new GameState(gs);

        return baseFunction.evaluate(maxplayer,minplayer,gs) + 
               baseFunction.evaluate(maxplayer,minplayer,gs2) * 0.5f;
    }
    
    public float upperBound(GameState gs) {
        return baseFunction.upperBound(gs)*1.5f;
    }
}
