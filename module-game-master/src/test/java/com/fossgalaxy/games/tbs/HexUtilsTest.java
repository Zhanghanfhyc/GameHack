package com.fossgalaxy.games.tbs;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.codetome.hexameter.core.api.CubeCoordinate;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.awt.*;

import static junit.framework.TestCase.assertEquals;
import static junitparams.JUnitParamsRunner.$;


/**
 * Created by webpigeon on 13/10/17.
 */
@RunWith(JUnitParamsRunner.class)
public class HexUtilsTest {

    public Object[] parametersForTestDistance() {
        return $(

                //1,1 in pixel co-orads
                //1,1 to all neibours:
                /**
                 * (167,103) (100,41)
                 1.0
                 (167,103) (62,119)
                 1.0
                 (167,103) (104,164)
                 1.0
                 (167,103) (186,166)
                 1.0
                 (167,103) (220,110)
                 1.0
                 (167,103) (196,51)
                 1.0
                 */

                $(new Point(167, 103), new Point(167, 103), 0),
                $(new Point(167, 103), new Point(100, 41), 1),
                $(new Point(167, 103), new Point(62, 119), 1),
                $(new Point(167, 103), new Point(104, 164), 1),
                $(new Point(167, 103), new Point(186, 166), 1),
                $(new Point(167, 103), new Point(220, 110), 1),
                $(new Point(167, 103), new Point(196, 51), 1),

                $(new Point(325, 298), new Point(259, 176), 2)

        );
    }

    @Test
    @Parameters(method = "parametersForTestDistance")
    public void testDistance(Point src, Point dest, int dist) {
        GameState state = new GameState(100, 100, null, 42, 2);
        CubeCoordinate srcCube = state.pix2cube(src);
        CubeCoordinate destCube = state.pix2cube(dest);


        int result = state.getDistance(srcCube, destCube);
        assertEquals(dist, result);
    }
}
