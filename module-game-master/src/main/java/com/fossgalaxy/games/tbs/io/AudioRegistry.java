package com.fossgalaxy.games.tbs.io;

import javax.sound.sampled.Clip;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by webpigeon on 22/01/18.
 */
public class AudioRegistry {
    //singletons = bad but time = limited.
    public static final AudioRegistry INSTANCE = new AudioRegistry();

    private Map<String, Clip> cache;

    public AudioRegistry() {
        this.cache = new HashMap<>();
    }

    public static boolean isSupported() {
        return false;
    }

    public void preload(String filename) {
        cache.put(filename, IOUtils.loadClip(filename));
    }

    public Clip getClip(String name) {
        return cache.computeIfAbsent(name, IOUtils::loadClip);
    }

    public void play(String name) {
        System.out.println("Trying to play clip");
        Clip clip = getClip(name);
        if (clip == null) {
            System.err.println("Clip not loaded");
            return;
        }

        if (clip.isRunning()) {
            clip.stop();
        }
        System.out.println("Frame Length: " + clip.getMicrosecondLength());
        clip.setFramePosition(0);
//    	clip.loop(Clip.LOOP_CONTINUOUSLY);
        clip.start();
        System.out.println("Clip playing: " + clip.getMicrosecondPosition());
    }

}
