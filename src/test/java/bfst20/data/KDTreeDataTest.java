package bfst20.data;

import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.LinePath;
import bfst20.logic.kdtree.KDTree;
import bfst20.logic.kdtree.Rect;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.List;


import static org.junit.jupiter.api.Assertions.*;

class KDTreeDataTest {

    static KDTreeData kdTreeData;
    static Rect rect;

    @BeforeAll
    static void setup() {
        kdTreeData = KDTreeData.getInstance();
        rect = new Rect();

    }

    @Test
    void getInstance() {
        assertEquals(kdTreeData, KDTreeData.getInstance());
    }

    @Test
    void getRect() {
        assertNotNull(kdTreeData.getRect());
    }

    @Test
    void saveRectValues() throws NoSuchFieldException {
        kdTreeData.saveRectValues(10, 15, 20, 25);

        assertEquals(10, kdTreeData.getRect().getMinLat());
        assertEquals(15, kdTreeData.getRect().getMaxLat());
        assertEquals(20, kdTreeData.getRect().getMinLon());
        assertEquals(25, kdTreeData.getRect().getMaxLon());
    }

    @Test
    void saveKDTree(){
        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(40, 45, 50, 55);
        linePaths.add(linePath);

        KDTree kdTree = new KDTree(linePaths, rect);

        kdTreeData.saveKDTree(OSMType.COASTLINE, kdTree);

        assertEquals(kdTreeData.getKDTree(OSMType.COASTLINE), kdTree);
    }

    @Test
    void getKDTree() {
        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(40, 45, 50, 55);
        linePaths.add(linePath);

        KDTree kdTree = new KDTree(linePaths, rect);
        kdTreeData.saveKDTree(OSMType.COASTLINE, kdTree);

        assertEquals(kdTreeData.getKDTree(OSMType.COASTLINE), kdTree);

        kdTreeData.clearData();
        assertEquals(kdTreeData.getKDTree(OSMType.COASTLINE), null);
    }

}