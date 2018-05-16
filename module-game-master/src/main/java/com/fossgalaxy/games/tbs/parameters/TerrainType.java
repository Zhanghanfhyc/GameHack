package com.fossgalaxy.games.tbs.parameters;

import com.fossgalaxy.games.tbs.entity.Entity;

import java.util.HashMap;
import java.util.Map;

public class TerrainType {

    private final String id;
    public final Map<String, Integer> requiredTags;
    public final String image;

    public TerrainType(String id){
        this.id = id;
        this.requiredTags = new HashMap<>();
        this.image = null;
    }

    public TerrainType(TerrainType tt){
        this.id = tt.id;
        this.requiredTags = new HashMap<>(tt.requiredTags);
        this.image = tt.image;
    }


    public String getID() {
        return id;
    }

    public String getImage() {
        return image;
    }

    public boolean isPassible(Entity entity) {
        if (requiredTags == null || requiredTags.isEmpty()){
            return false;
        }

        for ( Map.Entry<String, Integer> tagItr : requiredTags.entrySet() ) {

            String tag = tagItr.getKey();
            Integer terrainVal = tagItr.getValue();

            int entityVal = entity.getProperty( String.format("ter-%s", tag) );

            if (entityVal >= terrainVal) {
                return true;
            }

        }

        return false;
    }

    public boolean isPassible(EntityType entityType) {
        if (requiredTags == null || entityType == null){
            return false;
        }

        for ( Map.Entry<String, Integer> tagItr : requiredTags.entrySet() ) {

            String tag = tagItr.getKey();
            Integer terrainVal = tagItr.getValue();

            int entityTypeVal = entityType.getProperty( String.format("ter-%s", tag), 0);

            if (entityTypeVal >= terrainVal) {
                return true;
            }

        }

        return false;
    }

    void setTag(String tag, int minLevel) {
        requiredTags.put(tag, minLevel);
    }

    public String getName() {
        return id;
    }
}
