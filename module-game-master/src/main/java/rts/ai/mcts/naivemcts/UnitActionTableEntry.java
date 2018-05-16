/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.mcts.naivemcts;

import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;

import java.util.List;
import java.util.UUID;

/**
 *
 * @author santi
 */
public class UnitActionTableEntry {
    public UUID u;
    public int nactions = 0;
    public List<Order> actions = null;
    public double[] accum_evaluation = null;
    public int[] visit_count = null;
}
