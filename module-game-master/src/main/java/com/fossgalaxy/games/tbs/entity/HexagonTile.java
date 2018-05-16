package com.fossgalaxy.games.tbs.entity;

import com.fossgalaxy.games.tbs.parameters.TerrainType;
import org.codetome.hexameter.core.api.defaults.DefaultSatelliteData;

import java.awt.*;

/**
 * Created by webpigeon on 16/10/17.
 */
public class HexagonTile extends DefaultSatelliteData {
    private Color color;
    private String sprite;
    private TerrainType terrain;

    public HexagonTile(Color color, String sprite, TerrainType type) {
        this.color = color;
        this.sprite = sprite;
        this.terrain = type;
    }

    public String getSprite() {
        return sprite;
    }

    public Color getColor() {
        return color;
    }

    public TerrainType getTerrain() {
        return terrain;
    }

    public void setTerrain(TerrainType terrain) {
        this.terrain = terrain;
    }

    public boolean hasTerrain(TerrainType targetType) {
        if (targetType == null) {
            return terrain == null;
        }

        return targetType.equals(terrain);
    }

    public boolean isPassable(Entity us) {
        if (terrain == null){
            return false;
        }

        return terrain.isPassible(us);
    }
}
