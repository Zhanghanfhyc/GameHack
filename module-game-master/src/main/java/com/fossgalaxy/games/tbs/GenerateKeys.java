package com.fossgalaxy.games.tbs;

import com.fossgalaxy.games.tbs.ui.GameAction;
import com.fossgalaxy.object.ObjectFinder;

import java.util.Set;
import java.util.TreeSet;

public class GenerateKeys {

    public static void main(String[] args) {
        ObjectFinder<GameAction> gameActionObjectFinder = new ObjectFinder.Builder<>(GameAction.class)
                .scanNow()
                .build();

        System.out.println("Game Actions");
        Set<String> buildableObjects = new TreeSet<>(gameActionObjectFinder.getBuildableObjects());
        for(String object : buildableObjects){
            System.out.println(object);
        }
    }
}
