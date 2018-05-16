package com.fossgalaxy.games.tbs;

import com.fossgalaxy.games.tbs.ai.AIFactory;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.order.Order;
import com.fossgalaxy.games.tbs.order.OrderProcessor;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.games.tbs.ui.*;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.codetome.hexameter.core.api.Hexagon;

import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

/**
 * Hello world!
 */
public class AppTune {
    public static void main(String[] args) {

        if (args.length <= 2) {
            System.err.println("usage: [game] [level] [players...]");
            System.exit(1);
        }

        String gameName = args[0];
        String levelName = args[1];

        SettingsIO io = new SettingsIO();

		GameDef gameDef = io.loadGameDef(gameName);
		GameSettings common = io.loadSettings(gameDef);
		AIFactory factory = new AIFactory(io, gameDef.getEvalFileName(), gameDef.getRuleFileName(), gameDef.getAiFileName());

		MapDef map = io.loadMapDef(levelName);
        
        for (int attackValue = 0; attackValue <= 20; attackValue += 1) {
        	
        	//create our version
        	GameSettings version = new GameSettings(common);
        	version.setEntityProp("blue_archer", "atkRanged", attackValue);
        	version.finish(io);
        	
        	//create a copy of the map with our changes...
        	GameState game = map.buildState(version);

        	// HACK TODO FIX THIS
			for (ResourceType type : common.getResouceTypes()) {
				for (int i = 0; i < game.getNumberOfPlayers(); i++) {
					game.addResource(i, type, 200);
				}
			}
        	
        	int[] wins = new int[game.getNumberOfPlayers()];
            int turns = 0;  
        	
              for (int repeat=0; repeat<10; repeat++) {
              	
            	  //create controllers based on our modified game...
                  Controller[] controllers = new Controller[game.getNumberOfPlayers()];
                  for (int i=0; i<controllers.length; i++) {
                  	controllers[i] = factory.buildAI(args[i+2], version);
                  }
              	
                  //play the game and see who wins
                  GameState realState = new GameState(game);
                  
              	  Integer winner = runGame(realState, version, controllers);
              	  if (winner != Rule.NO_WINNER) {
              		  wins[winner]++;
              	  }
              	  turns += realState.getTime();
              	
              }
              
              System.out.println("Game over, "+attackValue+" win rates: "+Arrays.toString(wins)+" avg turns: "+turns/10.0);
        	
        }
      
    }
    
    public static Integer runGame(GameState level, GameSettings settings, Controller[] controllers) {
    	OrderProcessor processor = new OrderProcessor(level, controllers.length);
    	
    	for (Rule rule : settings.getVictoryConditions()) {
    		processor.addRule(rule);
    	}
    	
    	for (int i=0; i < settings.getTurnLimit(); i++) {
    		processor.setupTurn();
    		
    		int playerID = processor.getCurrentPlayer();
    		Controller controller = controllers[playerID];
    		
    		GameState stateClone = new GameState(level);
    		
    		Map<UUID, Order> orders = controller.doTurn(playerID, stateClone);
    		processor.doOrderBulk(orders);

    		processor.finishTurn();
    		
    		if (!Objects.equals(processor.getWinner(), Rule.NO_WINNER)) {
    			break;
    		}
    	}
    	
    	return processor.getWinner();
    }
    
}
