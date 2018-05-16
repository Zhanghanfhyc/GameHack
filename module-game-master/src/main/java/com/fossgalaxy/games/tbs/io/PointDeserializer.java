package com.fossgalaxy.games.tbs.io;

import com.google.gson.*;

import java.awt.*;
import java.lang.reflect.Type;

public class PointDeserializer implements JsonDeserializer<Point> {
    @Override
    public Point deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        JsonArray offsetArray = jsonElement.getAsJsonArray();
        if (offsetArray.size() != 2)
            throw new IllegalArgumentException("Must be two offsets: " + offsetArray.getAsString());
        return new Point(offsetArray.get(0).getAsInt(), offsetArray.get(1).getAsInt());
    }
}
