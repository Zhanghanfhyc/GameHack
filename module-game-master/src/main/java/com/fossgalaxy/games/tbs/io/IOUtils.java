package com.fossgalaxy.games.tbs.io;

import org.codetome.hexameter.core.api.CubeCoordinate;

import javax.imageio.ImageIO;
import javax.sound.sampled.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class IOUtils {
    private static final String IMG_PATH = "img/%s.png";
    private static final String WAV_PATH = "wav/%s.wav";

    public static BufferedImage loadImage(String filename) {
        URL url = IOUtils.class.getClassLoader().getResource(String.format(IMG_PATH, filename));
        if (url == null) {
            return null;
        }

        return loadImage(url);
    }

    public static BufferedImage loadImage(URL url) {
        try {
            return ImageIO.read(url);
        } catch (IOException ex){
            ex.printStackTrace();
            return null;
        }
    }

    public static Clip loadClip(String filename) {

        try {
            InputStream is = IOUtils.class.getClassLoader().getResourceAsStream(String.format(WAV_PATH, filename));
            if (is == null) {
                System.err.println("Input stream was null for: " + filename);
                return null;
            }

            AudioInputStream ais = AudioSystem.getAudioInputStream(is);

            Clip clip = AudioSystem.getClip();
            clip.open(ais);

            return clip;
        } catch (IOException | UnsupportedAudioFileException | LineUnavailableException e) {
            e.printStackTrace();
        }
        System.err.println("Still null for some reason");
        return null;
    }

    public static BufferedImage makeCompatable(BufferedImage img) {
        if (img == null) {
            return null;
        }

        GraphicsEnvironment env = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice device = env.getDefaultScreenDevice();
        GraphicsConfiguration config = device.getDefaultConfiguration();

        BufferedImage compat = config.createCompatibleImage(img.getWidth(), img.getHeight());
        Graphics g = compat.getGraphics();
        g.drawImage(img, 0, 0, null);
        g.dispose();
        return compat;
    }

    public static CubeCoordinate loc2cube(String loc) {
        String[] parts = loc.split(",");
        int[] coords = new int[parts.length];
        for (int i = 0; i < parts.length; i++) {
            coords[i] = Integer.parseInt(parts[i].trim());
        }
        if (coords.length == 2) {
            return CubeCoordinate.fromCoordinates(coords[0], coords[1]);
        } else if (coords.length == 3) {
            return CubeCoordinate.fromCoordinates(coords[0], coords[2]);
        }
        return null;
    }

    public static String cube2String(CubeCoordinate coordinate) {
        if (coordinate == null) {
            return "INVALID";
        }

        return String.format("(%d, %d, %d)", coordinate.getGridX(), coordinate.getGridY(), coordinate.getGridZ());
    }
}
