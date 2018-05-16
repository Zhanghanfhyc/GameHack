package com.fossgalaxy.games.tbs.entity;

import com.fossgalaxy.games.tbs.io.IOUtils;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import org.codetome.hexameter.core.api.CubeCoordinate;

/**
 * Created by webpigeon on 13/10/17.
 */
public class Resource {
    private final ResourceType type;
    private CubeCoordinate location;
    private int amountPerTurn;

    public Resource(ResourceType type, CubeCoordinate location, int amountPerTurn) {
        this.type = type;
        this.location = location;
        this.amountPerTurn = amountPerTurn;
    }

    public CubeCoordinate getLocation() {
        return location;
    }

    public ResourceType getType() {
        return type;
    }

    public int getAmountPerTurn() {
        return amountPerTurn;
    }


    @Override
    public String toString() {
        return String.format("%d %s at %s", amountPerTurn, type, IOUtils.cube2String(location));
    }

}
