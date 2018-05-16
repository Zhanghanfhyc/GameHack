package com.fossgalaxy.games.tbs.editor;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.HexagonTile;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.io.map.*;
import com.fossgalaxy.games.tbs.io.map2.MapDef;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

public class Builder {

    public static MapDef convertToData(GameState state) {
        MapDef data = new MapDef(state.getWidth(), state.getHeight(), 2, 60);

        //first, the terrain
        state.forAllHexagons(h ->  {
            HexagonTile ht = h.getSatelliteData().get();
            TerrainType tt = ht.getTerrain();
            if (tt != null) {
                data.setTerrian(h.getCubeCoordinate(), tt);
            }
        });

        //first, the resources
        for (Resource resource : state.getResources()) {
            data.addResource(resource);
        }

        //then, the entities
        for (Entity entity : state.getEntities()) {
            data.addEntity(entity);
        }

        return data;
    }

    public static void saveMapData(File file, MapDef data, SettingsIO io) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .registerTypeAdapter(CubeCoordinate.class, new CubeCoordSerializer())
                .create();

        try {
            PrintStream ps = new PrintStream(file);
            ps.println(gson.toJson(data));
            ps.close();
        }catch (FileNotFoundException ex) {
            ex.printStackTrace();
        }

    }
}
