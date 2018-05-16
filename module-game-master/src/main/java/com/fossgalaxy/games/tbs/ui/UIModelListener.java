package com.fossgalaxy.games.tbs.ui;

import com.fossgalaxy.games.tbs.GameState;
import com.fossgalaxy.games.tbs.entity.Entity;
import org.codetome.hexameter.core.api.CubeCoordinate;

public interface UIModelListener {

    public void onEntitySelected(Entity highlight);

    public default void onStateSelected(GameState state) {

    }

    public default void onLocationSelected(CubeCoordinate pos) {

    }

    public default void onTurnStart() {

    }

    public default void onTurnEnd() {

    }
}
