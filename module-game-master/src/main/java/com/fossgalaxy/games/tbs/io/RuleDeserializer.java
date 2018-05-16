package com.fossgalaxy.games.tbs.io;

import com.fossgalaxy.games.tbs.rules.Rule;
import com.fossgalaxy.object.ObjectFinder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonParseException;

import java.lang.reflect.Type;

public class RuleDeserializer implements JsonDeserializer<Rule> {

    /*
    FIXME - provide the ability to restict scanning to a package (and possibly it's dedendants)
    we should avoid scanning the whole class path to find 4 files :P
     */

    private ObjectFinder<Rule> creator;

    public RuleDeserializer(String[] packageList) {
    	this.creator = new ObjectFinder.Builder<>(Rule.class)
                .addPackage(packageList)
                //.setCache(SettingsIO.CACHE_FILE)
                .build();
    }
    
    @Override
    public Rule deserialize(JsonElement jsonElement, Type type, JsonDeserializationContext jsonDeserializationContext) throws JsonParseException {
        return creator.buildObject(jsonElement.getAsString());
    }
}
