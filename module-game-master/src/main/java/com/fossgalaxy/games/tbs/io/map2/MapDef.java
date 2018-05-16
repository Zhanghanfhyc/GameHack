package com.fossgalaxy.games.tbs.io.map2;

import java.util.*;

import org.codetome.hexameter.core.api.CubeCoordinate;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.parameters.GameSettings;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.parameters.TerrainType;

/**
 * Map format for JSON encoding.
 * 
 * This is much simpler than the existing map format, which is possible as the level editor removes a lot of the manual
 * work associated with creating the maps.
 * 
 * This format also does not require binding to types on map load, which means the same map can be loaded for multiple versions
 * of the parameters.
 * 
 * @author webpigeon
 *
 */
public class MapDef {
	
	private int width;
	private int height;
	private int players;
	private int hexSize;
	
	private final List<ResourceDef> resources;
	private final List<EntityDef> entities;
	private final Map<String, List<CubeCoordinate>> terrain;
	private final Map<Integer, Map<String, Integer>> startingResources;
	
	public MapDef(int width, int height, int players, int hexSize) {
		this.width = width;
		this.height = height;
		this.players = players;
		this.hexSize = hexSize;
		
		this.terrain = new HashMap<>();
		this.startingResources = new HashMap<>();

		this.resources = new ArrayList<>();
		this.entities = new ArrayList<>();
	}
	
	public GameState buildState(GameSettings settings) {
		
		GameState state = new GameState(width, height, settings, hexSize, players);
		
		for (Map.Entry<String, List<CubeCoordinate>> terrainEntry : terrain.entrySet()) {
			TerrainType type = settings.getTerrainType(terrainEntry.getKey());
			
			for (CubeCoordinate pos : terrainEntry.getValue()) {
				state.setTerrainAt(pos, type);
			}
		}
		
		for (ResourceDef resource : resources) {
			ResourceType type = settings.getResourceType(resource.getName());
			state.addResource(new Resource(type, resource.getLocation(), resource.getAmount()));
		}
		
		for (EntityDef entityDef : entities) {
			
			EntityType type = settings.getEntityType(entityDef.getType());
			Entity entity = new Entity(type, entityDef.getLocation(), entityDef.getOwner());
			entity.setProperties(entityDef.getProperties());
			
			state.addEntity(entity);
		}

		//intellij is wrong, can be null if gson is being annoying.
		if (startingResources != null) {
			for (int i = 0; i < players; i++) {
				Map<String, Integer> playerResources = startingResources.getOrDefault(players, Collections.emptyMap());

				for (ResourceType rt : settings.getResouceTypes()) {
					int startVal = playerResources.getOrDefault(rt.getName(), 0);
					state.setResource(i, rt, startVal);
				}
			}
		}

		return state;
	}

	public void setTerrian(CubeCoordinate cubeCoordinate, TerrainType tt) {
		List<CubeCoordinate> list = terrain.computeIfAbsent(tt.getID(), x -> new ArrayList<>());
		list.add(cubeCoordinate);
	}

	public void addResource(Resource resource) {
		ResourceDef def = new ResourceDef();
		def.setType(resource.getType().getName());
		def.setAmount(resource.getAmountPerTurn());
		def.setLocation(resource.getLocation());
		
		resources.add(def);
	}

	public int getStartingResource(int playerID, String resource){
		if (startingResources == null) {
			return 0;
		}

		Map<String, Integer> starting = startingResources.getOrDefault(playerID, Collections.emptyMap());
		return starting.getOrDefault(resource, 0);
	}

	public void setStartingResource(int playerID, String resource, int amount) {
		Map<String, Integer> startingResourcesForPlayer = startingResources.computeIfAbsent(playerID, e -> new HashMap<>());
		startingResourcesForPlayer.put(resource, amount);
	}

	public void addEntity(Entity entity) {
		EntityDef def = new EntityDef();
		def.setType(entity.getType().getName());
		def.setLocation(entity.getPos());
		def.setOwner(entity.getOwner());
		
		//include any custom properties.
		//FIXME this shouldn't include health if it's full
		Collection<String> propertyNames = entity.getPropertyNames();
		for (String property : propertyNames) {
			def.setPropery(property, entity.getProperty(property));
		}
		
		entities.add(def);
	}
}
