package com.fossgalaxy.games.tbs.io.map;

import com.fossgalaxy.games.tbs.io.IOUtils;
import com.google.gson.*;
import org.codetome.hexameter.core.api.CubeCoordinate;

import java.lang.reflect.Type;

/**
 * Created by webpigeon on 01/02/18.
 */
public class CubeCoordSerializer implements JsonSerializer<CubeCoordinate> {

    @Override
    public JsonElement serialize(CubeCoordinate coordinate, Type type, JsonSerializationContext jsonSerializationContext) {

        JsonObject json = new JsonObject();
        json.addProperty("x", coordinate.getGridX());
        json.addProperty("z", coordinate.getGridZ());

        return json;
    }
}
