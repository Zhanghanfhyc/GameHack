/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.mcts.uct;

import java.util.*;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import rts.*;

/**
 *
 * @author santi
 */
public class UCTUnitActionsNode {
    static Random r = new Random();
//    static float C = 50;   // this is the constant that regulates exploration vs exploitation, it must be tuned for each domain
//    static float C = 5;   // this is the constant that regulates exploration vs exploitation, it must be tuned for each domain
    static float C = 0.05f;   // this is the constant that regulates exploration vs exploitation, it must be tuned for each domain
    
    public int type;    // 0 : max, 1 : min, -1: Game-over
    UCTUnitActionsNode parent = null;
    public GameState gs;
    int depth = 0;
    
    public List<PlayerAction> actions = null;
    public List<UCTUnitActionsNode> children = null;
    float evaluation_bound = 0;
    float accum_evaluation = 0;
    int visit_count = 0;
    
    public UCTUnitActionsNode(int currPlayer, int ourID, GameState a_gs, UCTUnitActionsNode a_parent, float bound) {
        parent = a_parent;
        if (parent==null) depth = 0;
                     else depth = parent.depth+1;
        gs = a_gs;
        evaluation_bound = bound;


        if (gs.isGameOver()) {
            type = -1;
        } else if (currPlayer == ourID) {
            type = 0;
            actions = null;

            Collection<Entity> entities = gs.getOwnedEntities(currPlayer);
            for (Entity entity : entities) {
                actions = PlayerAction.GenerateForEntity(entity, gs);
                break;
            }

            if (actions==null) System.err.println("UCTUnitActionNode: error when generating maxplayer node!");
            children = new ArrayList<UCTUnitActionsNode>();
        } else {
            type = 1;
            actions = null;

            Collection<Entity> entities = gs.getOwnedEntities(currPlayer);
            for (Entity entity : entities) {
                actions = PlayerAction.GenerateForEntity(entity, gs);
                break;
            }
            if (actions==null) System.err.println("UCTUnitActionNode: error when generating minplayer node!");
            children = new ArrayList<UCTUnitActionsNode>();
        }     
    }
    
    public UCTUnitActionsNode UCTSelectLeaf(int currPlayer, int ourID, int max_depth) {
        // Cut the tree policy at a predefined depth
        if (depth>=max_depth) return this;        
        
        // if non visited children, visit:     
        if (children==null || actions==null) return this;
        if (children.size()<actions.size()) {
            PlayerAction a = actions.get(children.size());
            if (a!=null) {
                GameState gs2 = new GameState(gs);
                a.apply(gs2);
                UCTUnitActionsNode node = new UCTUnitActionsNode(currPlayer, ourID, new GameState(gs2), this, evaluation_bound);
                children.add(node);
                return node;                
            }
        }
        
        // Bandit policy:
        double best_score = 0;
        UCTUnitActionsNode best = null;
        for(int i = 0;i<children.size();i++) {
            UCTUnitActionsNode child = children.get(i);
            double exploitation = ((double)child.accum_evaluation) / child.visit_count;
            double exploration = Math.sqrt(Math.log(((double)visit_count)/child.visit_count));
            if (type==0) {
                // max node:
                exploitation = (exploitation + evaluation_bound)/(2*evaluation_bound);
            } else {
                exploitation = - (exploitation - evaluation_bound)/(2*evaluation_bound);
            }
//            System.out.println(exploitation + " + " + exploration);

            double tmp = C*exploitation + exploration;
            if (best==null || tmp>best_score) {
                best = child;
                best_score = tmp;
            }
        } 
        
        if (best==null) return this;
        return best.UCTSelectLeaf(currPlayer, ourID, max_depth);
    }    
    
    
    public void showNode(int depth, int maxdepth) {
        int mostVisitedIdx = -1;
        UCTUnitActionsNode mostVisited = null;
        for(int i = 0;i<children.size();i++) {
            UCTUnitActionsNode child = children.get(i);
            for(int j = 0;j<depth;j++) System.out.print("    ");
            System.out.println("child " + actions.get(i) + " explored " + child.visit_count + " Avg evaluation: " + (child.accum_evaluation/((double)child.visit_count)));
            if (depth<maxdepth) child.showNode(depth+1,maxdepth);
        }        
    }
}
