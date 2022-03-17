package bfst20.logic;

import bfst20.logic.entities.LinePath;
import bfst20.logic.entities.Node;
import bfst20.logic.entities.Relation;
import bfst20.logic.entities.Way;
import bfst20.logic.misc.OSMType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.lang.reflect.Method;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


public class LinePathGeneratorTest {
    private static LinePathGenerator linePathGenerator;
    private static AppController appController;
    private static Map<Long, Node> nodes;
    private static List<Relation> relations;
    private static List<Way> ways;

    private static Way way1;
    private static Way way2;
    private static Way way3;
    private static Relation relation;
    private static Node node1;

    @BeforeAll
    static void setup() {
        appController = mock(AppController.class);
        linePathGenerator = LinePathGenerator.getInstance(appController);
        relations = new ArrayList<>();
        ways = new ArrayList<>();
        nodes = new HashMap<>();
        node1 = new Node(1, (float) 55.6388937, (float) 12.6195664);
        Node node2 = new Node(2, (float) 55.6388541, (float) 12.6195888);
        Node node3 = new Node(3, (float) 55.6388636, (float) 12.6196557);
        Node node4 = new Node(4, (float) 55.6389036, (float) 12.6196342);
        Node node5 = new Node(5, (float) 55.6389305, (float) 12.6193886);
        Node node6 = new Node(6, (float) 55.6388968, (float) 12.6194073);
        Node node7 = new Node(7, (float) 55.6389019, (float) 12.6194476);
        Node node8 = new Node(8, (float) 55.6389368, (float) 12.6194316);
        Node node9 = new Node(9, (float) 55.6390171, (float) 12.6192845);
        Node node10 = new Node(10, (float) 55.6389979, (float) 12.6192956);
        Node node11 = new Node(11, (float) 55.6390061, (float) 12.6193435);
        Node node12 = new Node(12, (float) 55.6390253, (float) 12.6193344);

        way1 = new Way(13);
        way1.addNode(node1);
        way1.addNode(node2);
        way1.addNode(node3);
        way1.addNode(node4);
        way1.addNode(node1);
        way1.addNodeId(1);
        way1.addNodeId(2);
        way1.addNodeId(3);
        way1.addNodeId(4);
        way1.addNodeId(1);

        way1.setOSMType(OSMType.COASTLINE);


        way2 = new Way(14);
        way2.addNode(node5);
        way2.addNode(node6);
        way2.addNode(node7);
        way2.addNode(node8);
        way2.addNode(node5);

        way2.addNodeId(1);
        way2.addNodeId(2);
        way2.addNodeId(3);
        way2.addNodeId(4);
        way2.addNodeId(5);

        way2.setOSMType(OSMType.BUILDING);

        way3 = new Way(15);

        way3.addNode(node9);
        way3.addNode(node10);
        way3.addNode(node11);
        way3.addNode(node12);
        way3.addNode(node9);

        way3.addNodeId(1);
        way3.addNodeId(2);
        way3.addNodeId(3);
        way3.addNodeId(4);
        way3.addNodeId(5);

        way3.setOSMType(OSMType.FOREST);

        Relation relation1 = new Relation(32);
        relation1.addMember(13,"way");
        relation1.addMember(14,"way");
        relation1.setOSMType(OSMType.FOREST);
        relation1.setMultipolygon(true);

        Relation relation2 = new Relation(10);
        relation2.addMember(10,"way");
        relation2.addMember(11,"way");
        relation2.setOSMType(OSMType.FARMLAND);

        Relation relation3 = new Relation(10);
        relation3.addMember(10,"way");
        relation3.addMember(11,"way");
        relation3.setOSMType(OSMType.MEADOW);



        nodes.put( 1L, node1);
        nodes.put( 2L, node2);
        nodes.put(3L, node3);
        nodes.put(4L, node4);
        nodes.put(5L, node5);
        nodes.put(6L, node6);
        nodes.put(7L, node7);
        nodes.put(8L, node8);
        nodes.put(9L, node9);
        nodes.put(10L, node10);


        Collections.addAll(relations, relation1, relation2);
        Collections.addAll(ways, way1, way2, way3);

    }

    @Test
    void getInstance() {
        assertEquals(linePathGenerator, LinePathGenerator.getInstance(appController));
    }


    @Test
    void combine() throws Exception {

        Method method = LinePathGenerator.class.getDeclaredMethod("combineWays", Way.class, Way.class);
        method.setAccessible(true);
        Way way = (Way) method.invoke(linePathGenerator, way1, way2);
        assertEquals(way.getFirstNodeId(), 1);
        assertEquals(way.getLastNodeId(), 5);
    }
    @Test
    void combineSize() throws Exception {

        Method method = LinePathGenerator.class.getDeclaredMethod("combineWays", Way.class, Way.class);
        method.setAccessible(true);
        Way way = (Way) method.invoke(linePathGenerator, way1, way2);
        assertEquals(way.getFirstNodeId(), 1);
        assertEquals(way.getNodeIds().size(), 10);
    }


    @Test
    void convertWaysToLinePaths() {
        linePathGenerator.convertWaysToLinePaths(ways, nodes);

        verify(appController, times(2)).saveLinePathData(Mockito.any(OSMType.class), Mockito.any(LinePath.class));
    }

    @Test
    void convertRelationsToLinePaths() {
        linePathGenerator.convertWaysToLinePaths(ways, nodes);
        linePathGenerator.convertRelationsToLinePaths(relations);

        verify(appController, times(4)).saveLinePathData(Mockito.any(OSMType.class), Mockito.any(LinePath.class));
    }



}