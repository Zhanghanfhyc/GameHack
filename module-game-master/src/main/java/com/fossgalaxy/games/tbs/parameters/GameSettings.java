package com.fossgalaxy.games.tbs.parameters;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.object.ObjectFinder;

import java.util.*;
import java.util.stream.Collectors;

public class GameSettings {
    private static final Logger logger = LoggerFactory.getLogger(GameSettings.class);

    private final Map<String, ResourceType> resourceTypes;
    private final Map<String, EntityType> entityTypes;
    private final Map<String, TerrainType> terrainTypes;
    private final List<Rule> victoryConditions;
    
    private boolean finished;
    

    public GameSettings(){
        this.resourceTypes = new HashMap<>();
        this.entityTypes = new HashMap<>();
        this.terrainTypes = new HashMap<>();
        this.victoryConditions = new ArrayList<>();
            
        this.finished = false;
    }

    public GameSettings(GameSettings parent) {
        //resource types are immutable, therefore safe to copy
        this.resourceTypes = new HashMap<>(parent.resourceTypes);

        //clone entity types
        this.entityTypes = new HashMap<>();
        for (Map.Entry<String, EntityType> ete : parent.entityTypes.entrySet()) {
            entityTypes.put(ete.getKey(), new EntityType(ete.getValue()));
        }

        //clone terrain types
        this.terrainTypes = new HashMap<>();
        for (Map.Entry<String, TerrainType> ete : parent.terrainTypes.entrySet()) {
            terrainTypes.put(ete.getKey(), new TerrainType(ete.getValue()));
        }
        
        this.victoryConditions = new ArrayList<>(parent.victoryConditions);

        this.finished = parent.finished;
    }
    


    private void processEntityParents() {
        logger.trace("Start building entity hierarchy");

        Map<String, List<EntityType>> parentToChildren = entityTypes.values().stream()
                .filter(x -> x.get_extends() != null)
                .collect(Collectors.groupingBy(EntityType::get_extends));

        // These do not inherit anything and so are done
        Set<EntityType> done = entityTypes.values().stream()
                .filter(x -> x.get_extends() == null).distinct().collect(Collectors.toSet());

        for (EntityType doneEntity : done) {
            buildEntityParent(doneEntity, parentToChildren);
        }

        logger.trace("Finishing building entity hierarchy");
    }

    private void buildEntityParent(EntityType current, Map<String, List<EntityType>> parentToChildren) {
        List<EntityType> children = parentToChildren.getOrDefault(current.getName(), Collections.emptyList());
        if (children.isEmpty()) return;
        for (EntityType child : children) {
            child.setParent(current);
            buildEntityParent(child, parentToChildren);
        }
    }
    
    private void processEntityActions(SettingsIO io){
    	
    	for (EntityType type : entityTypes.values()) {
    		String[] actionDefs = type.get_actions();
    		
    		if (actionDefs != null) {
    			List<GameAction> actions = io.convertActions(actionDefs, this);
		    	type.setAllAvailableActions(actions);
    		}
    	}
    	
    }

    /**
     * Require that the settings are unfinished.
     *
     * This means that inheritance has not yet been calculated and it's safe to edit the properties.
     */
    private void requireUnfinished(){
        if (finished) {
            throw new IllegalStateException("These game paramters are finalised, no futher editing is possible");
        }
    }

    /**
     * Require that the settings are finished.
     *
     * This means inheritance has been run and it's safe to use this in a game.
     */
    private void requireFinished(){
        if (!finished) {
            throw new IllegalStateException("These game paramters not finalised, no refusing to provide type");
        }
    }

    public void addEntityType(EntityType entityType) {
        requireUnfinished();

        Objects.requireNonNull(entityType.getName());
        Objects.requireNonNull(entityType);

        entityTypes.put(entityType.getName(), entityType);
    }

    public void addResourceType(ResourceType type) {
        requireUnfinished();

        Objects.requireNonNull(type.getName());
        Objects.requireNonNull(type);

        resourceTypes.put(type.getName(), type);
    }

    public void addTerrainType(TerrainType type) {
        requireUnfinished();

        Objects.requireNonNull(type.getName());
        Objects.requireNonNull(type);

        terrainTypes.put(type.getName(), type);
    }


    public void setTerrainTag(String name, String tag, int minLevel) {
        requireUnfinished();

        TerrainType terrainType = terrainTypes.get(name);
        Objects.requireNonNull(terrainType);

        //TODO move terrain, entityType, resourceType and this class to live in the same place
        //terrainType.setTag(tag, minLevel);
    }

    public void setEntityProp(String name, String prop, int value){
        requireUnfinished();

        EntityType type = entityTypes.get(name);
        Objects.requireNonNull(type);

        type.setProperty(prop, value);
    }

    public void setEntityCost(String name, String cost, int value){
        requireUnfinished();

        EntityType type = entityTypes.get(name);
        Objects.requireNonNull(type);

        type.setCost(cost, value);
    }

	public void addVictoryCondition(Rule rule) {
		requireUnfinished();
		Objects.requireNonNull(rule);
		victoryConditions.add(rule);
	}
	
	public void removeVictoryCondition(Rule rule) {
		requireUnfinished();
		Objects.requireNonNull(rule);
		victoryConditions.remove(rule);
	}
    
    public EntityType getEntityType(String name){
        return getEntityType(name, true);
    }

    public TerrainType getTerrainType(String name){
        return getTerrainType(name, true);
    }

    public ResourceType getResourceType(String name){
        return getResourceType(name, true);
    }
    
    public List<Rule> getVictoryConditions(){
    	return getVictoryConditions(true);
    }

    public EntityType getEntityType(String name, boolean requireFinished){
        if (!requireFinished) requireFinished();
        return entityTypes.get(name);
    }

    public TerrainType getTerrainType(String name, boolean requireFinished){
        if (!requireFinished) requireFinished();
        return terrainTypes.get(name);
    }

    public ResourceType getResourceType(String name, boolean requireFinished){
        if (!requireFinished) requireFinished();
        return resourceTypes.get(name);
    }
    
    public List<Rule> getVictoryConditions(boolean requireFinished) {
    	if (!requireFinished) requireFinished();
    	return Collections.unmodifiableList(victoryConditions);
    }
    
    /**
     * Mark this game settings object as complete.
     * 
     * This will perform any steps needed to ensure that the game state is correctly created.
     * This makes the game settings object immutable.
     */
    public void finish(SettingsIO io){
        requireUnfinished();

        processEntityParents();
        processEntityActions(io);
        
        this.finished = true;
    }

    public boolean hasResourceType(String type) {
        return resourceTypes.containsKey(type);
    }

	public Collection<String> getResourceNames() {
		return Collections.unmodifiableSet(resourceTypes.keySet());
	}

	public int getTurnLimit() {
		return 10000; //TODO
	}

	public List<ResourceType> getResouceTypes() {
		return new ArrayList<>(resourceTypes.values());
	}


	public List<EntityType> getEntityTypes() {
		return new ArrayList<>(entityTypes.values());
	}
	

	public List<TerrainType> getTerrainTypes() {
		return new ArrayList<>(terrainTypes.values());
	}
	
}
