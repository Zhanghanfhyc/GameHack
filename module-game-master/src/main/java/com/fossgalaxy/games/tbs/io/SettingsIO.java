package com.fossgalaxy.games.tbs.io;

import com.fossgalaxy.games.tbs.GameDef;
import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.ai.Controller;
import com.fossgalaxy.games.tbs.ai.rules.PerEntityRule;
import com.fossgalaxy.games.tbs.ai.rules.ProductionRule;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.io.map.CubeCoordDeserializer;
import com.fossgalaxy.games.tbs.io.map.MapData;
import com.fossgalaxy.games.tbs.io.map.MapDeserializer;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.object.ObjectFinder;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializer;

import com.google.gson.reflect.TypeToken;

import org.codetome.hexameter.core.api.CubeCoordinate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import rts.ai.core.AI;
import rts.ai.evaluation.EvaluationFunction;

import java.awt.*;
import java.io.*;

import java.lang.reflect.Type;

import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * Created by webpigeon on 13/10/17.
 */
public class SettingsIO {
    private final Logger logger = LoggerFactory.getLogger(SettingsIO.class);
    public final static String CACHE_FILE = "cache/cache.xml";
    
    private final ObjectFinder<GameAction> actionFinder;
    private final String[] scanPackages;
    private final Gson gson;

    private GameSettings settings;

    public static SettingsIO buildWithExtras(String ... extrapkgs){
        List<String> pkgs = new ArrayList<>(Arrays.asList(extrapkgs));
        pkgs.add("com.fossgalaxy.games.tbs");
        pkgs.add("rts.ai");

        return new SettingsIO(pkgs.toArray(new String[pkgs.size()]));
    }

    public SettingsIO() {
        this("com.fossgalaxy.games.tbs", "rts.ai");
    }

    public SettingsIO(String ... packagesToInclude) {
    	this.scanPackages = packagesToInclude;
    	this.gson = new GsonBuilder()
                .registerTypeAdapter(MapData.class, new MapDeserializer(this))
                .registerTypeAdapter(Color.class, colorJsonDeserializer())
                .registerTypeAdapter(Rule.class, new RuleDeserializer(scanPackages))
                .registerTypeAdapter(Point.class, new PointDeserializer())
                .registerTypeAdapter(CubeCoordinate.class, new CubeCoordDeserializer())
                .create();

        actionFinder = buildActionFinder();
    }
    
    /**
     * Load a set of settings from disk
     * 
     * @param gameDef the game definition to load
     * @return the settings object (unfinished)
     */
    public GameSettings loadSettings(String gameDef) {
    	GameDef def = loadGameDef(gameDef);
    	return loadSettings(def);
    }

    public MapDef loadMapDef(String filename) {
    	return gson.fromJson(loadFile(filename), MapDef.class);
    }
    
    /**
     * Load settings based off a gamedef.
     * 
     * @param def the game definition
     * @return the settings object (unfinished)
     */
    public GameSettings loadSettings(GameDef def) {
    	GameSettings settings = new GameSettings();
    	
    	TerrainType[] terrains = gson.fromJson(loadFile(def.getTerrainFileName()), TerrainType[].class);
        for (TerrainType t : terrains) {
        	settings.addTerrainType(t);
        }
        
        ResourceType[] resources = gson.fromJson(loadFile(def.getResourcesFileName()), ResourceType[].class);
        for (ResourceType t : resources) {
        	settings.addResourceType(t);
        }
        
        EntityType[] entityTypes = Arrays.stream(def.getTypesFileName())
                .map(this::loadEntities)
                .flatMap(Arrays::stream)
                .toArray(EntityType[]::new);
        
        for (EntityType type : entityTypes) {
        	settings.addEntityType(type);
        }
        
        List<Rule> rules = def.getRules();
        for (Rule rule : rules) {
        	settings.addVictoryCondition(rule);
        }
        
    	return settings;
    }

    /**
     * Build an object finder for actions.
     */
    private ObjectFinder<GameAction> buildActionFinder() {
        ObjectFinder<GameAction> actionFinder = new ObjectFinder.Builder<>(GameAction.class)
                .addPackage(scanPackages)
//                .setCache(CACHE_FILE)
                .build();
        
        
        //this needs to point to the current settings object!
        actionFinder.addConverter(EntityType.class, this::getEntityType);
        actionFinder.addConverter(ResourceType.class, this::getResourceType);
        actionFinder.addConverter(TerrainType.class, this::getTerrainType);
        
        return actionFinder;
    }

    /**
     * Get an entity type from the current settings object.
     * 
     * @param name the name of the entity
     * @return the entity type, or null if none matches.
     */
    EntityType getEntityType(String name) {
    	return settings.getEntityType(name);
    }
    
    /**
     * Get a resource type from the current settings object.
     * 
     * @param name the name of the resource
     * @return the entity type, or null if none matches.
     */
    ResourceType getResourceType(String name) {
    	return settings.getResourceType(name);
    }
    
    /**
     * Get a terrain type from the current settings object.
     * 
     * @param name the name of the terrain
     * @return the entity type, or null if none matches.
     */
    public TerrainType getTerrainType(String name) {
    	return settings.getTerrainType(name);
    }

    public GameDef loadGameDef(String filename) {
    	GameDef def = gson.fromJson(loadFile(filename), GameDef.class);
    	if (def == null) {
    		throw new RuntimeException("could not load gamedef with name "+filename);
    	}
        return def;
    }

    private Reader loadFile(String filename) {
        try {
            File file = new File(filename);
            return new FileReader(file);
        } catch (FileNotFoundException ex) {
            return new InputStreamReader(SettingsIO.class.getClassLoader().getResourceAsStream(filename));
        }
    }


    private EntityType[] loadEntities(String filename) {
        return gson.fromJson(loadFile(filename), EntityType[].class);
    }

    public MapData loadMap(String filename) {
        return gson.fromJson(loadFile(filename), MapData.class);
    }

    private JsonDeserializer<Color> colorJsonDeserializer() {
        return (jsonElement, type, jsonDeserializationContext) -> {
            String[] parts = jsonElement.getAsString().split(",");
            if (parts.length != 3)
                throw new IllegalArgumentException("Should be 3 ints comma separated for a color. You provided: " + jsonElement.getAsString());
            int[] rgb = {
                    Integer.parseInt(parts[0]),
                    Integer.parseInt(parts[1]),
                    Integer.parseInt(parts[2])
            };
            for (int i = 0; i < rgb.length; i++) {
                if (rgb[i] < 0 || rgb[i] > 255)
                    throw new IllegalArgumentException("Color values out of range: " + jsonElement.getAsString());
            }
            return new Color(rgb[0], rgb[1], rgb[2]);
        };
    }

    /**
     * Convert actions from strings to action objects.
     * 
     * This needs to use the actionFinder - if I move this into GameSettings then when cloning gamesettings i'll need to
     * create copies of the actionFinder to bind it to the child gamesettings objects which means rescanning the classpath.
     * 
     * Instead, i'm temporarily replacing the settings with the one I want to build from, building and then putting it back.
     * Goes without saying this is not threadsafe... (or good practice come to think of it).
     * 
     * @param actionDefs
     * @param gameSettings
     * @return
     */
	public List<GameAction> convertActions(String[] actionDefs, GameSettings gameSettings) {
		GameSettings old = settings;
		
		settings = gameSettings;
		List<GameAction> actions = new ArrayList<>(actionDefs.length);
    	for (String actionDef : actionDefs) {
    		actions.add(actionFinder.buildObject(actionDef));
    	}
    	
    	settings = old;

		return actions;
	}
	
	public GameAction convertAction(String actionDef, GameSettings gameSettings) {
		GameSettings old = settings;
		
		settings = gameSettings;
    	GameAction action = actionFinder.buildObject(actionDef);
    	
    	settings = old;

		return action;
	}

    public void loadAliases(String filename, Map<String, String> map){
        Type type = new TypeToken<Map<String, String>>(){}.getType();
        map.putAll(gson.fromJson(loadFile(filename), type));
        
        System.out.println(map);
    }
	
	public String[] getPackageList() {
		return scanPackages;
	}

}
