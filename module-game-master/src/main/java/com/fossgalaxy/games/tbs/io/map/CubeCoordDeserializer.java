package com.fossgalaxy.games.tbs.io.map;

import com.fossgalaxy.games.tbs.io.IOUtils;
import com.google.gson.*;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.lang.reflect.Type;

/**
 * Created by webpigeon on 01/02/18.
 */
public class CubeCoordDeserializer implements JsonDeserializer<CubeCoordinate> {

    @Override
    public CubeCoordinate deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {

        if (jsonElement.isJsonArray()) {
            JsonArray arr = jsonElement.getAsJsonArray();
            if (arr.size() != 2){
                throw new IllegalArgumentException("x and z only must be provided");
            }

            return CubeCoordinate.fromCoordinates(arr.get(0).getAsInt(), arr.get(1).getAsInt());
        }

        if (jsonElement.isJsonObject()) {
            JsonObject obj = jsonElement.getAsJsonObject();
            return CubeCoordinate.fromCoordinates(obj.get("x").getAsInt(), obj.get("z").getAsInt());
        }

        return IOUtils.loc2cube(jsonElement.getAsString());
    }
}
