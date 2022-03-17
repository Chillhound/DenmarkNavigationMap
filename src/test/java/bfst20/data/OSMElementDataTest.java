package bfst20.data;

import bfst20.logic.entities.Bounds;
import bfst20.logic.entities.Node;
import bfst20.logic.entities.Relation;
import bfst20.logic.entities.Way;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class OSMElementDataTest {

    static OSMElementData osmElementData;

    @BeforeAll
    static void setup() {
        osmElementData = OSMElementData.getInstance();
    }

    @Test
    void getInstance() {
        assertEquals(osmElementData, OSMElementData.getInstance());
    }

    @Test
    void saveRelation() {
        Relation relation = new Relation(23232);

        osmElementData.saveRelation(relation);

        assertEquals(relation, osmElementData.getRelations().get(0));
    }

    @Test
    void getRelations() {
        osmElementData.getRelations().clear();

        Relation relation = new Relation(1123);
        Relation relation2 = new Relation(23322);

        osmElementData.saveRelation(relation);
        osmElementData.saveRelation(relation2);


        assertEquals(2, osmElementData.getRelations().size());
    }

    @Test
    void saveBounds() {
        Bounds bounds = new Bounds(12, 14, 16, 18);

        osmElementData.saveBounds(bounds);

        assertEquals(12, osmElementData.getBounds().getMaxLat());
        assertEquals(14, osmElementData.getBounds().getMinLat());
        assertEquals(16, osmElementData.getBounds().getMaxLon());
        assertEquals(18, osmElementData.getBounds().getMinLon());
    }

    @Test
    void getBounds() {
        Bounds bounds = new Bounds(12, 14, 16, 18);

        osmElementData.saveBounds(bounds);

        assertNotNull(osmElementData.getBounds());
    }

    @Test
    void addToNodeMap() {
        long id = 12345;
        Node node = new Node(1, 1);

        osmElementData.addToNodeMap(id, node);

        assertNotNull(osmElementData.getNodes().get(id));
    }

    @Test
    void getNodes() {
        long id = 2223;
        Node node = new Node(1, 1);

        osmElementData.addToNodeMap(id, node);

        assertEquals(node, osmElementData.getNodes().get(id));
        assertNotNull(osmElementData.getNodes());
    }

    @Test
    void getNode() {
    }

    @Test
    void saveWay() {
        osmElementData.getWays().clear();
        Way way = new Way();

        osmElementData.saveWay(way);

        assertEquals(way, osmElementData.getWays().get(0));
    }

    @Test
    void getWays() {
        Way way = new Way();
        Way way2 = new Way();

        osmElementData.saveWay(way);
        osmElementData.saveWay(way2);

        assertNotNull(osmElementData.getWays());
    }
}