package com.fossgalaxy.games.tbs.io.map2;

import java.util.HashMap;
import java.util.Map;

import org.codetome.hexameter.core.api.CubeCoordinate;

public class EntityDef {
	private String type;
	private CubeCoordinate location;
	private int owner;
	private Map<String, Integer> properties;
	
	public EntityDef() {
		this.properties = new HashMap<>();
	}
	
	public String getType() {
		return type;
	}

	public CubeCoordinate getLocation() {
		return location;
	}

	public int getOwner() {
		return owner;
	}

	public Map<String, Integer> getProperties() {
		return properties;
	}

	public void setType(String type) {
		this.type = type;
	}

	public void setLocation(CubeCoordinate pos) {
		this.location = pos;
	}
	
	public void setOwner(int owner) {
		this.owner = owner;
	}
	
	public void setPropery(String key, int value) {
		this.properties.put(key, value);
	}

}
