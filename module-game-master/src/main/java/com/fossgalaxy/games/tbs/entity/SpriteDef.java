package com.fossgalaxy.games.tbs.entity;

import java.awt.*;

public class SpriteDef {
    private String image;
    private float scale = 1f;
    // In degrees
    private int rotation = 0;
    private Point offset = new Point(0, 0);

    public String getImage() {
        return image;
    }

    public float getScale() {
        return scale;
    }

    public int getRotation() {
        return rotation;
    }

    public Point getOffset() {
        return offset;
    }

    @Override
    public String toString() {
        return "SpriteDef{" +
                "image='" + image + '\'' +
                ", scale=" + scale +
                ", rotation=" + rotation +
                ", offset=" + offset +
                '}';
    }
}
