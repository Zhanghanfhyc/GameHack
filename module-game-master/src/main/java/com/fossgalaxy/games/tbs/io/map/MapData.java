package com.fossgalaxy.games.tbs.io.map;

import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MapData {

    private final List<Entity> entities = new ArrayList<>();
    private final List<Resource> resources = new ArrayList<>();

    private int width;
    private int height;

    private int players;
    private CubeCoordinate[] startLocations;
    private String defaultTileBg;

    //horribly inefficient
    private Map<CubeCoordinate, TerrainType> terrain;

    public MapData(){
        this.terrain = new HashMap<>();
    }

    public MapData(int width, int height) {
        this();

        this.width = width;
        this.height = height;
    }

    public void setTerrian(CubeCoordinate coordinate, TerrainType type){
        terrain.put(coordinate, type);
    }

    public TerrainType getTerrian(CubeCoordinate coordinate){
        return terrain.getOrDefault(coordinate, null);
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void addResource(Resource resource) {
        resources.add(resource);
    }

    @Override
    public String toString() {
        return "MapData{" +
                "width=" + width +
                ", height=" + height +
                ", entities=" + entities +
                ", resources=" + resources +
                '}';
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public List<Resource> getResources() {
        return resources;
    }

    public String getDefaultTileType() {
        return defaultTileBg;
    }

    public int getPlayers() {
        return players;
    }

    public CubeCoordinate[] getStartPositions() {
        return startLocations;
    }

    public void setStartPositions(CubeCoordinate[] startPositions) {
        this.startLocations = startPositions;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setPlayers(int nPlayers){
        this.players = nPlayers;
    }

    public void setTileBG(String tileBG){
        this.defaultTileBg = tileBG;
    }

    public Map<String, List<CubeCoordinate>> getRevTerrain() {
        Map<String, List<CubeCoordinate>> revMap = new HashMap<>();

        for (Map.Entry<CubeCoordinate, TerrainType> entry : terrain.entrySet()) {
            TerrainType t = entry.getValue();
            if (t == null) {
                continue;
            }

            List<CubeCoordinate> coordinate = revMap.computeIfAbsent(t.getID(), e -> new ArrayList<>());
            coordinate.add(entry.getKey());
        }

        return revMap;
    }
}
