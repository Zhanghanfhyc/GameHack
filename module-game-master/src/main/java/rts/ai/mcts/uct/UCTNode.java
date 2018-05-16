/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.mcts.uct;

import java.util.*;

import com.fossgalaxy.games.tbs.GameState;
import rts.PlayerAction;
import utils.MoveGenerator;

/**
 *
 * @author santi
 */
public class UCTNode {
    static Random r = new Random();
    public static float C = 0.05f;   // this is the constant that regulates exploration vs exploitation, it must be tuned for each domain
//    public static float C = 1;   // this is the constant that regulates exploration vs exploitation, it must be tuned for each domain
    
    public int type;    // 0 : max, 1 : min, -1: Game-over
    UCTNode parent = null;
    public GameState gs;
    int depth = 0;  // the depth in the tree
    
    boolean hasMoreActions = true;
    MoveGenerator moveGenerator = null;
    public List<PlayerAction> actions = null;
    public List<UCTNode> children = null;
    float evaluation_bound = 0;
    float accum_evaluation = 0;
    int visit_count = 0;
    
    
    public UCTNode(int currPlayer, int ourID, GameState a_gs, UCTNode a_parent, float bound) throws Exception {
        parent = a_parent;
        gs = a_gs;
        if (parent==null) depth = 0;
                     else depth = parent.depth+1;        
        evaluation_bound = bound;


        if (gs.isGameOver()) {
            type = -1;
        } else if (currPlayer == ourID) {
            type = 0;
            moveGenerator = new MoveGenerator(a_gs, currPlayer);
            actions = new ArrayList<>();
            children = new ArrayList<>();
        } else {
            type = 1;
            moveGenerator = new MoveGenerator(a_gs, currPlayer);
            actions = new ArrayList<>();
            children = new ArrayList<>();
        }
    }
    
    public UCTNode UCTSelectLeaf(int currPlayer, int ourID, long cutOffTime, int max_depth) throws Exception {
        
        // Cut the tree policy at a predefined depth
        if (depth>=max_depth) return this;        

        int nextPlayer = currPlayer==ourID?1-ourID:ourID;

        // if non visited children, visit:        
        if (hasMoreActions) {
            if (moveGenerator==null) {
//                System.out.println("No more leafs because moveGenerator = null!");
                return this;
            }

            PlayerAction childAction = moveGenerator.generateNextAction();

            if (childAction!=null) {
                actions.add(childAction);
                GameState clone = new GameState(gs);

                childAction.apply(clone);

                UCTNode node = new UCTNode(nextPlayer, ourID, clone, this, evaluation_bound);
                children.add(node);
                return node;                
            } else {
                hasMoreActions = false;
            }
        }
        
        // Bandit policy:
        double best_score = 0;
        UCTNode best = null;
        for (UCTNode child : children) {
            double tmp = childValue(child);
            if (best==null || tmp>best_score) {
                best = child;
                best_score = tmp;
            }
        } 
        
        if (best==null) {
//            System.out.println("No more leafs because this node has no children!");
//            return null;
            return this;
        }
        return best.UCTSelectLeaf(nextPlayer, ourID, cutOffTime, max_depth);
//        return best;
    }    
    
        
    public double childValue(UCTNode child) {
        double exploitation = ((double)child.accum_evaluation) / child.visit_count;
        double exploration = Math.sqrt(Math.log((double)visit_count)/child.visit_count);
        if (type==0) {
            // max node:
            exploitation = (evaluation_bound + exploitation)/(2*evaluation_bound);
        } else {
            exploitation = (evaluation_bound - exploitation)/(2*evaluation_bound);
        }
//            System.out.println(exploitation + " + " + exploration);

        double tmp = C*exploitation + exploration;
        return tmp;
    }
    
    
    public void showNode(int depth, int maxdepth) {
        int mostVisitedIdx = -1;
        UCTNode mostVisited = null;
        for(int i = 0;i<children.size();i++) {
            UCTNode child = children.get(i);
            for(int j = 0;j<depth;j++) System.out.print("    ");
            System.out.println("child explored " + child.visit_count + " Avg evaluation: " + (child.accum_evaluation/((double)child.visit_count)) + " : " + actions.get(i));
            if (depth<maxdepth) child.showNode(depth+1,maxdepth);
        }        
    }
}
