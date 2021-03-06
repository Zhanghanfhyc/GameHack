/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.mcts.naivemcts;

import java.math.BigInteger;
import java.util.*;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.order.Order;
import rts.*;
import rts.ai.mcts.MCTSNode;
import utils.MoveGenerator;
import utils.Sampler;

/**
 *
 * @author santi
 */
public class NaiveMCTSNode extends MCTSNode {
    
    public static final int E_GREEDY = 0;
    public static final int UCB1 = 1;
    
    static public int DEBUG = 0;
    
    public static float C = 0.05f;   // exploration constant for UCB1
    
    boolean forceExplorationOfNonSampledActions = true;
    boolean hasMoreActions = true;
    public MoveGenerator moveGenerator = null;
    HashMap<BigInteger,NaiveMCTSNode> childrenMap = new LinkedHashMap<BigInteger,NaiveMCTSNode>();    // associates action codes with children
    // Decomposition of the player actions in unit actions, and their contributions:
    public List<UnitActionTableEntry> unitActionTable = null;
    double evaluation_bound;    // this is the maximum positive value that the evaluation function can return
    public BigInteger multipliers[];


    public NaiveMCTSNode(int currPlayer, int ourID, GameState a_gs, NaiveMCTSNode a_parent, double a_evaluation_bound, int a_creation_ID, boolean fensa) throws Exception {
        parent = a_parent;
        gs = a_gs;
        if (parent==null) depth = 0;
                     else depth = parent.depth+1;     
        evaluation_bound = a_evaluation_bound;
        creation_ID = a_creation_ID;
        forceExplorationOfNonSampledActions = fensa;
        
        if (gs.isGameOver()) {
            type = -1;
        } else if (currPlayer == ourID) {
            type = 0;
            moveGenerator = new MoveGenerator(gs, currPlayer);
            actions = new ArrayList<PlayerAction>();
            children = new ArrayList<MCTSNode>();
            unitActionTable = new LinkedList<UnitActionTableEntry>();
            multipliers = new BigInteger[moveGenerator.getChoices().size()];
            BigInteger baseMultiplier = BigInteger.ONE;
            int idx = 0;
            for (Map.Entry<UUID, List<Order>> choice : moveGenerator.getChoices().entrySet()) {
                UnitActionTableEntry ae = new UnitActionTableEntry();
                ae.u = choice.getKey();
                ae.nactions = choice.getValue().size();
                ae.actions = choice.getValue();
                ae.accum_evaluation = new double[ae.nactions];
                ae.visit_count = new int[ae.nactions];
                for (int i = 0; i < ae.nactions; i++) {
                    ae.accum_evaluation[i] = 0;
                    ae.visit_count[i] = 0;
                }
                unitActionTable.add(ae);
                multipliers[idx] = baseMultiplier;
                baseMultiplier = baseMultiplier.multiply(BigInteger.valueOf(ae.nactions));
                idx++;
             }
        } else {
            type = 1;
            moveGenerator = new MoveGenerator(gs, currPlayer);
            actions = new ArrayList<PlayerAction>();
            children = new ArrayList<MCTSNode>();
            unitActionTable = new LinkedList<UnitActionTableEntry>();
            multipliers = new BigInteger[moveGenerator.getChoices().size()];
            BigInteger baseMultiplier = BigInteger.ONE;
            int idx = 0;
            for (Map.Entry<UUID, List<Order>> choice : moveGenerator.getChoices().entrySet()) {
                UnitActionTableEntry ae = new UnitActionTableEntry();
                ae.u = choice.getKey();
                ae.nactions = choice.getValue().size();
                ae.actions = choice.getValue();
                ae.accum_evaluation = new double[ae.nactions];
                ae.visit_count = new int[ae.nactions];
                for (int i = 0; i < ae.nactions; i++) {
                    ae.accum_evaluation[i] = 0;
                    ae.visit_count[i] = 0;
                }
                unitActionTable.add(ae);
                multipliers[idx] = baseMultiplier;
                baseMultiplier = baseMultiplier.multiply(BigInteger.valueOf(ae.nactions));
                idx++;
            }
        }
    }

    
    // Naive Sampling:
    public NaiveMCTSNode selectLeaf(int maxplayer, int minplayer, float epsilon_l, float epsilon_g, float epsilon_0, int global_strategy, int max_depth, int a_creation_ID) throws Exception {
        if (unitActionTable == null) return this;
        if (depth>=max_depth) return this;       
        
        /*
        // DEBUG:
        for(PlayerAction a:actions) {
            for(Pair<Unit,UnitAction> tmp:a.getActions()) {
                if (!gs.getUnits().contains(tmp.m_a)) new Error("DEBUG!!!!");
                boolean found = false;
                for(UnitActionTableEntry e:unitActionTable) {
                    if (e.u == tmp.m_a) found = true;
                }
                if (!found) new Error("DEBUG 2!!!!!");
            }
        } 
        */
        
        if (children.size()>0 && r.nextFloat()>=epsilon_0) {
            // sample from the global MAB:
            NaiveMCTSNode selected = null;
            if (global_strategy==E_GREEDY) selected = selectFromAlreadySampledEpsilonGreedy(epsilon_g);
            else if (global_strategy==UCB1) selected = selectFromAlreadySampledUCB1(C);
            return selected.selectLeaf(maxplayer, minplayer, epsilon_l, epsilon_g, epsilon_0, global_strategy, max_depth, a_creation_ID);
        }  else {
            // sample from the local MABs (this might recursively call "selectLeaf" internally):
            return selectLeafUsingLocalMABs(maxplayer, minplayer, epsilon_l, epsilon_g, epsilon_0, global_strategy, max_depth, a_creation_ID);
        }
    }
   

    
    public NaiveMCTSNode selectFromAlreadySampledEpsilonGreedy(float epsilon_g) throws Exception {
        if (r.nextFloat()>=epsilon_g) {
            NaiveMCTSNode best = null;
            for(MCTSNode pate:children) {
                if (type==0) {
                    // max node:
                    if (best==null || (pate.accum_evaluation/pate.visit_count)>(best.accum_evaluation/best.visit_count)) {
                        best = (NaiveMCTSNode)pate;
                    }                    
                } else {
                    // min node:
                    if (best==null || (pate.accum_evaluation/pate.visit_count)<(best.accum_evaluation/best.visit_count)) {
                        best = (NaiveMCTSNode)pate;
                    }                                        
                }
            }

            return best;
        } else {
            // choose one at random from the ones seen so far:
            NaiveMCTSNode best = (NaiveMCTSNode)children.get(r.nextInt(children.size()));
            return best;
        }
    }
    
    
    public NaiveMCTSNode selectFromAlreadySampledUCB1(float C) throws Exception {
        NaiveMCTSNode best = null;
        double bestScore = 0;
        for(MCTSNode pate:children) {
            double exploitation = ((double)pate.accum_evaluation) / pate.visit_count;
            double exploration = Math.sqrt(Math.log((double)visit_count)/pate.visit_count);
            if (type==0) {
                // max node:
                exploitation = (evaluation_bound + exploitation)/(2*evaluation_bound);
            } else {
                exploitation = (evaluation_bound - exploitation)/(2*evaluation_bound);
            }
    //            System.out.println(exploitation + " + " + exploration);

            double tmp = C*exploitation + exploration;            
            if (best==null || tmp>bestScore) {
                best = (NaiveMCTSNode)pate;
                bestScore = tmp;
            }
        }
        
        return best;
    }    
    
    
    public NaiveMCTSNode selectLeafUsingLocalMABs(int maxplayer, int minplayer, float epsilon_l, float epsilon_g, float epsilon_0, int global_strategy, int max_depth, int a_creation_ID) throws Exception {   
        PlayerAction pa2;
        BigInteger actionCode;       

        // For each unit, rank the unitActions according to preference:
        List<double []> distributions = new LinkedList<double []>();
        List<Integer> notSampledYet = new LinkedList<Integer>();
        for(UnitActionTableEntry ate:unitActionTable) {
            if (ate.nactions == 0) {
                continue;
            }

            double []dist = new double[ate.nactions];
            int bestIdx = -1;
            double bestEvaluation = 0;
            int visits = 0;
            for(int i = 0;i<ate.nactions;i++) {
                if (type==0) {
                    // max node:
                    if (bestIdx==-1 || 
                        (visits!=0 && ate.visit_count[i]==0) ||
                        (visits!=0 && (ate.accum_evaluation[i]/ate.visit_count[i])>bestEvaluation)) {
                        bestIdx = i;
                        if (ate.visit_count[i]>0) bestEvaluation = (ate.accum_evaluation[i]/ate.visit_count[i]);
                                             else bestEvaluation = 0;
                        visits = ate.visit_count[i];
                    }
                } else {
                    // min node:
                    if (bestIdx==-1 || 
                        (visits!=0 && ate.visit_count[i]==0) ||
                        (visits!=0 && (ate.accum_evaluation[i]/ate.visit_count[i])<bestEvaluation)) {
                        bestIdx = i;
                        if (ate.visit_count[i]>0) bestEvaluation = (ate.accum_evaluation[i]/ate.visit_count[i]);
                                             else bestEvaluation = 0;
                        visits = ate.visit_count[i];
                    }
                }
                dist[i] = epsilon_l/ate.nactions;
            }
            if (ate.visit_count[bestIdx]!=0) {
                dist[bestIdx] = (1-epsilon_l) + (epsilon_l/ate.nactions);
            } else {
                if (forceExplorationOfNonSampledActions) {
                    for(int j = 0;j<dist.length;j++) 
                        if (ate.visit_count[j]>0) dist[j] = 0;
                }
            }  

            if (DEBUG>=3) {
                System.out.print("[ ");
                for(int i = 0;i<ate.nactions;i++) System.out.print("(" + ate.visit_count[i] + "," + ate.accum_evaluation[i]/ate.visit_count[i] + ")");
                System.out.println("]");
                System.out.print("[ ");
                for(int i = 0;i<dist.length;i++) System.out.print(dist[i] + " ");
                System.out.println("]");
            }

            notSampledYet.add(distributions.size());
            distributions.add(dist);
        }

        // Select the best combination that results in a valid playeraction by epsilon-greedy sampling:

        pa2 = new PlayerAction();
        actionCode = BigInteger.ZERO;
        while(!notSampledYet.isEmpty()) {
            int i = notSampledYet.remove(r.nextInt(notSampledYet.size()));

            try {
                UnitActionTableEntry ate = unitActionTable.get(i);
                int code;
                Order ua;

                // try one at random:
                double []distribution = distributions.get(i);
                code = Sampler.weighted(distribution);
                ua = ate.actions.get(code);

                pa2.addUnitAction(ate.u, ua);

                actionCode = actionCode.add(BigInteger.valueOf(code).multiply(multipliers[i]));

            } catch(Exception e) {
                e.printStackTrace();
            }
        }   

        NaiveMCTSNode pate = childrenMap.get(actionCode);
        if (pate==null) {
            actions.add(pa2);
            GameState gs2 = new GameState(gs);
            pa2.apply(gs2);

            NaiveMCTSNode node = new NaiveMCTSNode(maxplayer, minplayer, new GameState(gs2), this, evaluation_bound, a_creation_ID, forceExplorationOfNonSampledActions);
            childrenMap.put(actionCode,node);
            children.add(node);          
            return node;                
        }

        return pate.selectLeaf(maxplayer, minplayer, epsilon_l, epsilon_g, epsilon_0, global_strategy, max_depth, a_creation_ID);
    }
    
    
    public UnitActionTableEntry getActionTableEntry(UUID u) {
        for(UnitActionTableEntry e:unitActionTable) {
            if (e.u.equals(u)) return e;
        }
        throw new Error("Could not find Action Table Entry!");
    }


    public void propagateEvaluation(double evaluation, NaiveMCTSNode child) {
        accum_evaluation += evaluation;
        visit_count++;
        
//        if (child!=null) System.out.println(evaluation);

        // update the unitAction table:
        if (child != null) {
            int idx = children.indexOf(child);
            PlayerAction pa = actions.get(idx);

            for (Map.Entry<UUID, Order> ua : pa.getOrders().entrySet()) {
                UnitActionTableEntry actionTable = getActionTableEntry(ua.getKey());
                idx = actionTable.actions.indexOf(ua.getValue());

                if (idx==-1) {
                    System.out.println("Looking for action: " + ua.getValue());
                    System.out.println("Available actions are: " + actionTable.actions);
                }
                
                actionTable.accum_evaluation[idx] += evaluation;
                actionTable.visit_count[idx]++;
            }
        }

        if (parent != null) {
            ((NaiveMCTSNode)parent).propagateEvaluation(evaluation, this);
        }
    }

    public void printUnitActionTable() {
        for (UnitActionTableEntry uat : unitActionTable) {
            System.out.println("Actions for unit " + uat.u);
            for (int i = 0; i < uat.nactions; i++) {
                System.out.println("   " + uat.actions.get(i) + " visited " + uat.visit_count[i] + " with average evaluation " + (uat.accum_evaluation[i] / uat.visit_count[i]));
            }
        }
    }    
}