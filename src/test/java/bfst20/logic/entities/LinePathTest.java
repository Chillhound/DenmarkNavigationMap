package bfst20.logic.entities;

import bfst20.logic.LinePathGenerator;
import bfst20.logic.misc.OSMType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;


import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class LinePathTest {
    public static LinePath linePath1;
    public static LinePath linePath2;
    public static LinePath linePath3;
    public static Way way1;

    @BeforeAll
    public static void setup() {
        Map<Long, Node> nodes1 = new HashMap<>();
        nodes1.put((long) 1, new Node(1, 10, 9));
        nodes1.put((long) 2, new Node(2, 11, 8));
        way1 = new Way(10);
        way1.addNodeId(1);
        way1.addNodeId(2);


        linePath1 = new LinePath(30, 20, 15, 10);
        linePath2 = new LinePath(way1, OSMType.BUILDING, nodes1, true);
        linePath3 = new LinePath(way1, OSMType.BUILDING, nodes1, true);
    }

    @Test
    public void isMultipolygon() {
        assertEquals(linePath1.isMultipolygon(), false);
        assertEquals(linePath2.isMultipolygon(), false);
    }

    @Test
    public void setMultipolygon() {
        linePath1.setMultipolygon(true);
        assertEquals(linePath1.isMultipolygon(), true);

        linePath1.setMultipolygon(false);
        assertEquals(linePath1.isMultipolygon(), false);

        linePath2.setMultipolygon(true);
        assertEquals(linePath2.isMultipolygon(), true);

        linePath2.setMultipolygon(false);
        assertEquals(linePath2.isMultipolygon(), false);
    }

    @Test
    public void getBounds() {
        assertEquals(linePath1.getBounds().getMaxLat(), 30);
        assertEquals(linePath1.getBounds().getMaxLon(), 20);
        assertEquals(linePath1.getBounds().getMinLat(), 15);
        assertEquals(linePath1.getBounds().getMinLon(), 10);
    }

    @Test
    public void getCenterLatitude() {

        assertEquals(linePath1.getCenterLatitude(), 22.5);
        assertEquals(linePath2.getCenterLatitude(), 10.5);
    }

    @Test
    public void getCenterLongitude() {
        assertEquals(linePath1.getCenterLongitude(), 15);
        assertEquals(linePath2.getCenterLongitude(), 8.5);
    }

    @Test
    public void getMaxX() {
        assertEquals(linePath2.getMaxX(), 11);
        assertEquals(linePath1.getMaxX(), 30);
    }

    @Test
    public void getMaxY() {
        assertEquals(linePath2.getMaxY(), 9);
        assertEquals(linePath1.getMaxY(), 20);
    }


    @Test
    public void getMinX() {
        assertEquals(linePath2.getMinX(), 10);
        assertEquals(linePath1.getMinX(), 15);
    }

    @Test
    public void getMinY() {
        assertEquals(linePath2.getMinY(), 8);
        assertEquals(linePath1.getMinY(), 10);
    }

    @Test
    public void getCoords() {
        float[] coords = new float[]{9, 10, 8, 11};
        assertArrayEquals(linePath2.getCoords(), coords);
    }

    @Test
    public void getFill() {
        assertEquals(linePath2.getFill(), true);
    }

    @Test
    public void getName() {
        assertEquals(linePath2.getName(), null);
    }

    @Test
    public void getOSMType() {
        assertEquals(linePath2.getOSMType(), OSMType.BUILDING);
    }

    @Test
    public void getWay() {
        assertEquals(linePath2.getWay(), way1);
    }

    @Test
    public void getWayId(){
        assertEquals(linePath2.getWayId(), 10);
    }

    @Test
    public void setWayNull() {
        assertEquals(linePath3.getWay(),way1);
        assertEquals(linePath3.getWayId(),10);
        linePath3.removeWay();
        assertEquals(linePath3.getWay(), null);
        assertEquals(linePath3.getWayId(),0);
    }
}
