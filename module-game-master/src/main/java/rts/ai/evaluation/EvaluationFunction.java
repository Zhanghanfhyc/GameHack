/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.evaluation;

import com.fossgalaxy.games.tbs.GameState;

/**
 *
 * @author santi
 */
public abstract class EvaluationFunction {    
    public static float VICTORY = 10000;
    
    public abstract float evaluate(int maxplayer, int minplayer, GameState gs);
    public abstract float upperBound(GameState gs);
    
    public String toString() {
        return getClass().getSimpleName();
    }
}
