package com.fossgalaxy.games.tbs.entity;

import com.fossgalaxy.games.tbs.io.IOUtils;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Created by webpigeon on 13/10/17.
 */
public class Entity {
    private final UUID uuid;
    private EntityType type;
    private CubeCoordinate location;
    private int owner;

    private Map<String, Integer> properties;

    public Entity() {
        this(new EntityType(), CubeCoordinate.fromCoordinates(0, 0), 0);
    }

    public Entity(EntityType type, CubeCoordinate location, int owner) {
        this.uuid = UUID.randomUUID();

        this.type = type;
        this.location = location;
        this.owner = owner;
        this.properties = new HashMap<>();

        properties.put("health", type.getProperty("health", 0));
    }

    public Entity(Entity entity) {
        this.uuid = entity.uuid;
        this.type = entity.type;
        this.location = entity.location;
        this.owner = entity.owner;
        this.properties = new HashMap<>(entity.properties);
    }

    public CubeCoordinate getPos() {
        return location;
    }

    public void setPos(CubeCoordinate pos) {
        this.location = pos;
    }

    public double getHealthFrac() {
        return getHealth() / (type.getProperty("health", 1) * 1.0);
    }

    public int getHealth() {
        return properties.getOrDefault("health", 0);
    }

    public void setHealth(int health) {
        properties.put("health", health);
    }

    public int getOwner() {
        return owner;
    }

    public int getProperty(String name){
        return getProperty(name, 0);
    }

    public int getProperty(String name, int defaultVal) {
        Integer myVal = properties.get(name);

        if (myVal == null) {
            return type.getProperty(name, defaultVal);
        }

        return myVal;
    }

    public void setProperty(String name, int val) {
        properties.put(name, val);
    }

    @Override
    public String toString() {
        return String.format("%s at %s with %d health", type, IOUtils.cube2String(location), getHealth());
    }

    public EntityType getType() {
        return type;
    }

    public void setType(EntityType to) {
        this.type = to;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Entity entity = (Entity) o;

        return uuid != null ? uuid.equals(entity.uuid) : entity.uuid == null;
    }

    @Override
    public int hashCode() {
        return uuid != null ? uuid.hashCode() : 0;
    }

    public UUID getID() {
        return uuid;
    }

    public Collection<String> getPropertyNames() {
        return properties.keySet();
    }

    public void setOwner(int owner) {
        this.owner = owner;
    }

    public void removeProperty(String oldName) {
        properties.remove(oldName);
    }

    public void setProperties(Map<String, Integer> properties) {
        this.properties.putAll(properties);
    }
}
