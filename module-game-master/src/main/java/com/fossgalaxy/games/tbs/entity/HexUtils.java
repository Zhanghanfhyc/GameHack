package com.fossgalaxy.games.tbs.entity;

import org.codetome.hexameter.core.api.HexagonalGridBuilder;

/**
 * Created by webpigeon on 13/10/17.
 */
public class HexUtils {

    public static final HexagonalGridBuilder<HexagonTile> buildGrid(int width, int height, int hexSize) {
        return new HexagonalGridBuilder<HexagonTile>()
                .setGridHeight(height)
                .setGridWidth(width)
                .setRadius(hexSize);
    }


}
