package com.fossgalaxy.games.tbs.io.map;

import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.google.gson.*;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class MapSerializer implements JsonSerializer<MapData> {
    private final SettingsIO settingsIO;
    private Gson unModifiedGson;

    public MapSerializer(SettingsIO settingsIO) {
        this.unModifiedGson = new Gson();
        this.settingsIO = settingsIO;
    }


    @Override
    public JsonElement serialize(MapData mapData, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject elm = new JsonObject();

        elm.addProperty("width", mapData.getWidth());
        elm.addProperty("height", mapData.getHeight());
        elm.addProperty("players", mapData.getPlayers());
        elm.addProperty("defaultTileBg", mapData.getDefaultTileType());
        elm.add("startPositions", jsonSerializationContext.serialize(mapData.getStartPositions()));

        // save resources
        JsonArray resourceArray = new JsonArray();
        for (Resource resource : mapData.getResources()) {

            JsonObject object = new JsonObject();
            object.addProperty("name", resource.getType().getName());
            object.addProperty("amountPerTurn", resource.getAmountPerTurn());
            object.add("loc", jsonSerializationContext.serialize(resource.getLocation()));

            resourceArray.add(object);
        }
        elm.add("resource", resourceArray);

        // save terrain
        JsonObject terrainArray = new JsonObject();
        Map<String, List<CubeCoordinate>> terMap = mapData.getRevTerrain();
        for (Map.Entry<String, List<CubeCoordinate>> terEntry : terMap.entrySet()) {

            JsonArray terArr = new JsonArray();
            for (CubeCoordinate coordinate : terEntry.getValue()) {
                terArr.add(jsonSerializationContext.serialize(coordinate));
            }

            terrainArray.add(terEntry.getKey(), terArr);
        }
        elm.add("terrainIndex", terrainArray);


        // save entities
        JsonArray entityArray = new JsonArray();
        for (Entity entity : mapData.getEntities()) {

            JsonObject object = new JsonObject();
            object.addProperty("name", entity.getType().getName());
            object.addProperty("player", entity.getOwner());
            object.add("loc", jsonSerializationContext.serialize(entity.getPos()));

            JsonObject entityProps = new JsonObject();
            List<String> names = new ArrayList<>(entity.getPropertyNames());
            Collections.sort(names);

            for (String name : names) {
                entityProps.addProperty(name, entity.getProperty(name));
            }
            object.add("properties", entityProps);

            entityArray.add(object);
        }
        elm.add("entity", entityArray);

        return elm;
    }
}
