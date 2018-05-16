package com.fossgalaxy.games.tbs.io.map;

import com.fossgalaxy.games.tbs.entity.Entity;
import com.fossgalaxy.games.tbs.parameters.EntityType;
import com.fossgalaxy.games.tbs.entity.Resource;
import com.fossgalaxy.games.tbs.parameters.ResourceType;
import com.fossgalaxy.games.tbs.io.SettingsIO;
import com.fossgalaxy.games.tbs.parameters.TerrainType;
import com.google.gson.*;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;

import static com.fossgalaxy.games.tbs.io.IOUtils.loc2cube;

public class MapDeserializer implements JsonDeserializer<MapData> {
    private final SettingsIO settingsIO;
    private Gson unModifiedGson;

    public MapDeserializer(SettingsIO settingsIO) {
        this.unModifiedGson = new Gson();
        this.settingsIO = settingsIO;
    }

    @Override
    public MapData deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext context) throws JsonParseException {
        JsonObject root = jsonElement.getAsJsonObject();
        MapData data = unModifiedGson.fromJson(root, MapData.class);


//        if (root.has("terrainIndex")) {
//            JsonObject terrain = root.get("terrainIndex").getAsJsonObject();
//            Map<String, TerrainType> terrainTypeMap = settingsIO.getTerrainTypes();
//
//            for (String name : terrain.keySet()) {
//
//                JsonArray loc = terrain.get(name).getAsJsonArray();
//                for (JsonElement elm : loc) {
//                    CubeCoordinate cubeCoordinate = context.deserialize(elm, CubeCoordinate.class);
//                    data.setTerrian(cubeCoordinate, terrainTypeMap.getOrDefault(name, null));
//                }
//
//            }
//        }

//        JsonElement entitiesElm = root.get("entity");
//
//        if (entitiesElm.isJsonObject()) {
//            JsonObject entities = entitiesElm.getAsJsonObject();
//
//            // Handle standalone objects
//            if (entities.has("single")) {
//                JsonArray single = entities.getAsJsonArray("single");
//                processEntities(single, data, context);
//            }
//
//            // handle repeated objects
//            if (entities.has("repeated")) {
//                JsonArray repeated = entities.getAsJsonArray("repeated");
//                processEntities(repeated, data, context);
//            }
//
//            //TODO handle ranges - May not be possible
//            JsonArray ranges = entities.getAsJsonArray("ranges");
//            if (ranges != null) {
//                for (JsonElement element : ranges) {
//                    JsonObject entityData = element.getAsJsonObject();
//                    String name = entityData.get("name").getAsString();
//                    EntityType entityType = settingsIO.getEntityType(name);
//                    int owner = entityData.get("player").getAsInt();
//                    CubeCoordinate start = loc2cube(entityData.get("start").getAsString());
//                    CubeCoordinate end = loc2cube(entityData.get("end").getAsString());
//                    // Iterate through these somehow???
//                }
//            }
//        } else if (entitiesElm.isJsonArray()) {
//            JsonArray entityArray = entitiesElm.getAsJsonArray();
//            processEntities(entityArray, data, context);
//        } else {
//            throw new RuntimeException("Unknown JSON type for entities");
//        }
//
//        JsonElement resourceElm = root.get("resource");
//        if (resourceElm.isJsonObject()) {
//            JsonObject resources = root.getAsJsonObject("resource");
//            if (resources.has("single")) {
//                JsonArray single = resources.getAsJsonArray("single");
//                processResources(single, data, context);
//            }
//
//            if (resources.has("repeated")) {
//                JsonArray repeated = resources.getAsJsonArray("repeated");
//                processResources(repeated, data, context);
//            }
//        } else if (resourceElm.isJsonArray()) {
//            JsonArray resources = root.getAsJsonArray("resource");
//            processResources(resources, data, context);
//        } else {
//            throw new RuntimeException("Unknown JSON type for resources");
//        }
//
//
//        //set start positions
//        CubeCoordinate[] positions = context.deserialize(root.get("startPositions"), CubeCoordinate[].class);
//        data.setStartPositions(positions);

        return data;
    }

    private void processEntities(JsonArray array, MapData data, JsonDeserializationContext context){
//       for (JsonElement element : array) {
//           JsonObject entityData = element.getAsJsonObject();
//
//           EntityType entityType = settingsIO.getEntityType(entityData.get("name").getAsString());
//           int owner = entityData.get("player").getAsInt();
//
//
//           Map<String, Integer> entityProperties = new HashMap<>();
//
//           if (entityData.has("properties")) {
//               JsonObject entityPropJson = entityData.get("properties").getAsJsonObject();
//               for (Map.Entry<String, JsonElement> entry : entityPropJson.entrySet()) {
//                   entityProperties.put(entry.getKey(), entry.getValue().getAsInt());
//               }
//           }
//
//           if (entityData.has("locs")) {
//
//               CubeCoordinate[] locs = context.deserialize(entityData.get("locs"), CubeCoordinate[].class);
//               for (CubeCoordinate loc : locs) {
//
//                   Entity e = new Entity(entityType, loc, owner);
//                   e.setProperties(entityProperties);
//
//                   data.addEntity(e);
//               }
//
//           }
//
//           if (entityData.has("loc")) {
//               CubeCoordinate loc = context.deserialize(entityData.get("loc"), CubeCoordinate.class);
//
//               Entity e = new Entity(entityType, loc, owner);
//               e.setProperties(entityProperties);
//
//               data.addEntity(e);
//           }
//       }

    }

    private void processResources(JsonArray array, MapData data, JsonDeserializationContext context) {

//        for (JsonElement element : array) {
//            JsonObject resourceData = element.getAsJsonObject();
//            String name = resourceData.get("name").getAsString();
//            ResourceType resourceType = settingsIO.getResourceType(resourceData.get("name").getAsString());
//            int amount = resourceData.get("amountPerTurn").getAsInt();
//
//            if (resourceData.has("loc")) {
//                CubeCoordinate loc = context.deserialize(resourceData.get("loc"), CubeCoordinate.class);
//                data.addResource(new Resource(resourceType, loc, amount));
//            }
//
//            if (resourceData.has("locs")) {
//                CubeCoordinate[] locs = context.deserialize(resourceData.get("locs"), CubeCoordinate[].class);
//                for (CubeCoordinate loc : locs)
//                    data.addResource(new Resource(resourceType, loc, amount));
//            }
//
//        }

    }

}
