package com.fossgalaxy.games.tbs.io.map2;

import org.codetome.hexameter.core.api.CubeCoordinate;

public class ResourceDef {
	
	private String name;
	private int amount;
	private CubeCoordinate location;

	public String getName() {
		return name;
	}

	public int getAmount() {
		return amount;
	}

	public CubeCoordinate getLocation() {
		return location;
	}

	public void setType(String name) {
		this.name = name;
	}

	public void setAmount(int amountPerTurn) {
		this.amount = amountPerTurn;
	}

	public void setLocation(CubeCoordinate location) {
		this.location = location;
	}

}
