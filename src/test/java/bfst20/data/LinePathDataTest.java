package bfst20.data;

import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.LinePath;
import bfst20.logic.entities.Node;
import bfst20.logic.entities.Way;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


class LinePathDataTest {

    static LinePathData linePathData;
    static Node node;

    @BeforeAll
    static void setup() {
        linePathData = LinePathData.getInstance();
    }

    @Test
    void getInstance() {
        assertEquals(linePathData, LinePathData.getInstance());
    }

    @Test
    void getLinePaths() {
        LinePath linePath = new LinePath(new Way(), OSMType.COASTLINE, new HashMap<>(), true);

        linePathData.saveLinePath(OSMType.COASTLINE, linePath);

        assertEquals(linePath, linePathData.getCoastlines().get(0));
    }

    @Test
    void saveLinePath() {
        LinePath linePath = new LinePath(new Way(), OSMType.MOTORWAY, new HashMap<>(), true);

        linePathData.saveLinePath(OSMType.MOTORWAY, linePath);

        assertEquals(linePath, linePathData.getMotorways().get(0));
    }

    @Test
    void saveLinePaths() {
        LinePath linePath = new LinePath(new Way(), OSMType.MOTORWAY, new HashMap<>(), true);
        List<LinePath> motorWays = new ArrayList<>();
        motorWays.add(linePath);

        Map<OSMType, List<LinePath>> linePaths = new HashMap<>();
        linePaths.put(OSMType.MOTORWAY, motorWays);
        linePathData.saveLinePaths(linePaths);

        assertEquals(linePath, linePathData.getLinePaths().get(OSMType.MOTORWAY).get(0));
    }

    @Test
    void addNodeTo() {
        linePathData.addNodeTo(OSMType.FOREST, node, new Way());

        assertEquals(1, linePathData.getNodeTo(OSMType.FOREST).size());
    }

    @Test
    void addToNodeTo() {
        linePathData.addNodeTo(OSMType.FARMLAND, node, new Way());

        assertEquals(1, linePathData.getNodeTo(OSMType.FARMLAND).size());
    }


    @Test
    void getNodeToCoastline() {
        linePathData.addNodeTo(OSMType.COASTLINE, node, new Way());

        assertEquals(1, linePathData.getNodeTo(OSMType.COASTLINE).size());
    }


    @Test
    void removeWayFromNodeTo() {
        linePathData.removeWayFromNodeTo(OSMType.FOREST, node);

        assertEquals(0, linePathData.getNodeTo(OSMType.FOREST).size());
    }


    @Test
    void save_getHighways() {
        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(new Way(), OSMType.COASTLINE, new HashMap<>(), true);
        linePaths.add(linePath);

        linePathData.saveHighways(linePaths);
        assertEquals(linePathData.getHighways(),linePaths);
    }

   
}