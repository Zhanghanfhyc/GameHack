package com.fossgalaxy.games.tbs.parameters;

import com.fossgalaxy.games.tbs.entity.SpriteDef;
import com.fossgalaxy.games.tbs.ui.GameAction;

import java.util.*;

/**
 * Created by webpigeon on 13/10/17.
 */
public class EntityType {

    private String name;

    private Map<String, Integer> cost;
    private Map<String, Integer> properties;

    private SpriteDef sprite;

    private EntityType parent;
    private String _extends;

    private List<GameAction> allAvailableActions;
    private String[] _actions;

    public EntityType() {
        this.cost = new HashMap<>();
        this.properties = new HashMap<>();
        this.allAvailableActions = new ArrayList<>();
    }

    public EntityType(EntityType parent){
    	this.name = parent.name;
        this.cost = new HashMap<>(parent.cost);
        this.properties = new HashMap<>(parent.properties);
        this.sprite = parent.sprite;
        
        this._extends = parent._extends;
        
        //if the thing we are cloning has actions, we should copy them.
        if (parent._actions != null) {
        	this._actions = new String[parent._actions.length];
        	System.arraycopy(parent._actions, 0, this._actions, 0, parent._actions.length);
        }
        
        this.allAvailableActions = new ArrayList<>(parent.allAvailableActions);
    }

    public Integer getProperty(String name) {
        return properties.get(name);
    }

    public int getProperty(String name, int defaultVal) {
        return properties.getOrDefault(name, defaultVal);
    }

    public String toString() {
        return name;
    }

    public String getName() {
        return name;
    }

    public SpriteDef getSprite() {
        return sprite;
    }

    public Map<String, Integer> getCosts() {
        if (cost == null) {
            return Collections.emptyMap();
        }

        return cost; //TODO
    }

    public boolean isInstance(EntityType type){
        return equals(type) || hasParent(type);
    }

    public boolean hasParent(EntityType type) {
        if (parent == null) {
            return false;
        }

        return parent.equals(type) || parent.hasParent(type);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EntityType that = (EntityType) o;
        return Objects.equals(name, that.name);
    }

    @Override
    public int hashCode() {

        return Objects.hash(name);
    }

    public String get_extends() {
        return _extends;
    }

    public void setParent(EntityType parent) {
        assert (this.parent == null);
        if (parent == this)
            throw new IllegalArgumentException("Parent cannot be same as child. You get into trouble for that you know");
        this.parent = parent;
        if (this.parent == null) return;

        for (Map.Entry<String, Integer> parentEntry : this.parent.properties.entrySet()) {
            this.properties.putIfAbsent(parentEntry.getKey(), parentEntry.getValue());
        }

        for (Map.Entry<String, Integer> parentEntry : this.parent.cost.entrySet()) {
            this.cost.putIfAbsent(parentEntry.getKey(), parentEntry.getValue());
        }

        if (parent._actions != null) {
            if (this._actions != null) {
                String[] newActions = new String[this._actions.length + this.parent._actions.length];
                System.arraycopy(this.parent._actions, 0, newActions, 0, this.parent._actions.length);
                System.arraycopy(this._actions, 0, newActions, this.parent._actions.length, this._actions.length);
                this._actions = newActions;
            } else {
                this._actions = new String[this.parent._actions.length];
                System.arraycopy(this.parent._actions, 0, this._actions, 0, this.parent._actions.length);
            }
        }
        this.allAvailableActions.addAll(this.parent.getAvailableActions());

    }

    public String[] get_actions() {
        return _actions;
    }

    public void setAllAvailableActions(List<GameAction> allAvailableActions) {
        assert (this.allAvailableActions == null);
        this.allAvailableActions = allAvailableActions;
    }

    public List<GameAction> getAvailableActions() {
        if (this.allAvailableActions == null) {
            return Collections.emptyList();
        }

        return this.allAvailableActions;
    }

    void setProperty(String prop, int value) {
        properties.put(prop, value);
    }

    void setCost(String resource, int value) {
        cost.put(resource, value);
    }
}
