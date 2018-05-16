package com.fossgalaxy.games.tbs.io;

import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by webpigeon on 22/01/18.
 */
public class SpriteRegistry {
    //singletons = bad but time = limited.
    public static final SpriteRegistry INSTANCE = new SpriteRegistry();

    private Map<String, BufferedImage> cache;

    public SpriteRegistry() {
        this.cache = new HashMap<>();
    }

    public void preload(String filename) {
        cache.put(filename, IOUtils.loadImage(filename));
    }

    public BufferedImage getImage(String name) {
        return cache.computeIfAbsent(name, IOUtils::loadImage);
    }
}
