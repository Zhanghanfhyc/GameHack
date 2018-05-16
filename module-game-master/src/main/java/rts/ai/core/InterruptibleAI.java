/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.core;

import com.fossgalaxy.games.tbs.GameState;
import rts.PlayerAction;

/**
 *
 * @author santi
 * 
 * - A "InterruptibleAI" is one that can divide the computation across multiple game frames. As such, it must implement the three
 * basic methods described below. 
 * - Usually, this AI is used in combination with the "ContinuingAI" class
 * 
 */
public interface InterruptibleAI {
    
    public void startNewComputation(int player, GameState gs) throws Exception;
    public void computeDuringOneGameFrame() throws Exception;
    public PlayerAction getBestActionSoFar() throws Exception;
}

