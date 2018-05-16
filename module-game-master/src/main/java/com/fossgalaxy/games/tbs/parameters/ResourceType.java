package com.fossgalaxy.games.tbs.parameters;

import java.awt.*;

/**
 * Created by webpigeon on 13/10/17.
 */
public class ResourceType {
    private final String name;
    private final Color color;
    private final String image;

    public ResourceType(String name, Color color, String image) {
        this.name = name;
        this.color = color;
        this.image = image;
    }

    public String getName() {
        return name;
    }

    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return name + " " + color.toString();
    }

    public String getImage() {
        return image;
    }


}
