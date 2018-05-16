/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package rts.ai.evaluation;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.ResourceType;

import java.util.*;
import java.util.stream.Collectors;

/**
 * I've got no idea what this does, but i've tried to port it anyway - JWR.
 *
 * @author santi
 */
public class LanchesterEvaluationFunction extends EvaluationFunction {    
    public static Float[] W_BASE      = {0.12900641042498262f, 0.48944975377829392f};
    public static Float[] W_RAX       = {0.23108197488337265f, 0.55022866772062451f};
    public static Float[] W_WORKER    = {0.18122298329807154f, -0.0078514695699861588f};
    public static Float[] W_LIGHT     = {1.7496678034331925f, 0.12587241165484406f};
    public static Float[] W_RANGE     = {1.6793840344563218f, 0.029918374064639004f};
    public static Float[] W_HEAVY     = {3.9012441116439427f, 0.16414240458460899f};
    public static Float[] W_MINERALS_CARRIED  = {0.3566229669443759f, 0.01061490087512941f};
    public static Float[] W_MINERALS_MINED    = {0.30141654836442761f, 0.38643842595899713f};

    //TODO populate these
    public static final Map<EntityType, Float[]> values = new HashMap<>();
    public static final List<EntityType> unitTypes = Arrays.asList();
    public static final List<EntityType> rangedUnitTypes = Arrays.asList();
    public static final List<EntityType> bases = Arrays.asList();

    public static float order = 1.7f;


    public static float sigmoid(float x) {
        return (float) (1.0f/( 1.0f + Math.pow(Math.E,(0.0f - x))));
      }
    
    public float evaluate(int maxplayer, int minplayer, GameState gs) {
    	return 2.0f*sigmoid(base_score(maxplayer,gs) - base_score(minplayer,gs))-1.0f;
    }

    public float base_score(int player, GameState gs) {
    	int index = 0;

    	//removed: if map is exactly 128 wide, then use case 1, some form of map detection maybe?.

    	float score = 0.0f;
    	float score_buildings = 0.0f;
        float nr_units = 0.0f;
        float res_carried = 0.0f;

        Collection<Entity> myEntities = gs.getOwnedEntities(player);
		Map<EntityType, List<Entity>> entityMap = myEntities.stream().collect(Collectors.groupingBy(Entity::getType));

		for (Map.Entry<EntityType, List<Entity>> entry : entityMap.entrySet()) {

			EntityType type = entry.getKey();
			List<Entity> entities = entry.getValue();

			Float[] value = values.getOrDefault(entry.getKey(), W_BASE);


			for (Entity entity : entities) {
				//XXX for bases, workers and ranged, this doesn't seem to be normalised...

                if ( unitTypes.contains(type) ) {
                    nr_units++;
                    if (rangedUnitTypes.contains(type)) {
                        score += entity.getHealth() * value[index];
                    } else {
                        score += entity.getHealthFrac() * value[index];
                    }
                }

                if (bases.contains(type)) {
                    score_buildings += entity.getHealth() * value[index];
                }

			}

		}

		double resourceMined = 0;
		Collection<String> resources = gs.getSettings().getResourceNames();
		for (String rt : resources) {
		    resourceMined += gs.getResource(player, rt);
        }


        score = (float) (score * Math.pow(nr_units, order-1));
        score += score_buildings + res_carried * W_MINERALS_CARRIED[index] + 
        		resourceMined * W_MINERALS_MINED[index];
        
        return score;
    }    
    
    public float upperBound(GameState gs) {
        return 2.0f;
    }
}
