package bfst20.logic;

import bfst20.data.RoutingData;
import bfst20.logic.entities.*;
import bfst20.logic.kdtree.KDTree;
import bfst20.logic.kdtree.Rect;
import bfst20.logic.misc.OSMType;
import bfst20.logic.routing.Edge;
import bfst20.logic.routing.Graph;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.Label;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static bfst20.logic.entities.LinePathTest.way1;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

class AppControllerTest {
    public static AppController appController;
    public static Map<OSMType, KDTree> tree;
    public static Graph graph;
    public static List<Edge> route;
    public static RoutingData routingData;
    public static Map<String, Double> routeInfo;
    public static Node node1;
    public static Node node2;
    public static Edge edge;
    public static Relation relation;


    @BeforeAll
    public static void setup() {
        appController = new AppController();
        appController.clearAllNonBinData();
        tree = new HashMap<>();

        //Used for save_fetch routedata & routeInfo
        node1 = new Node(1, (float) 55.6388937, (float) 12.6195664);
        node2 = new Node(2, (float) 55.6388541, (float) 12.6195888);
        edge = new Edge(OSMType.MOTORWAY, node1, node2, 100, "Farstrupvej", 50, false);
        routingData = RoutingData.getInstance();
        route = new ArrayList<>();
        routeInfo = new HashMap<>();
        route.add(edge);
        routeInfo.put("Farstrupvej", 100.0);
        relation = new Relation(10);

    }


    @Test
    public void save_fetchALLKDTrees() {
        Rect rect = new Rect(0, 10, 0, 10);
        LinePath p1 = createTestLinePath(5, 5, 6, 6);
        LinePath p2 = createTestLinePath(6, 6, 7, 7);
        List<LinePath> paths = new ArrayList<>();
        paths.add(p1);
        paths.add(p2);
        KDTree kdtree = new KDTree(paths, rect);
        tree.put(OSMType.BUILDING, kdtree);

        appController.saveAllKDTrees(tree);
        assertEquals(appController.fetchAllKDTrees(), tree);
    }

    //Not a test, but a auto creation for linepaths that is used in test save_fetchAllKDTrees()
    public LinePath createTestLinePath(float fLat, float fLon, float tLat, float tLon) {
        Map<Long, Node> nodes1 = new HashMap<>();
        nodes1.put((long) 1, new Node(1, fLat, fLon));
        nodes1.put((long) 2, new Node(2, tLat, tLon));
        Way way1 = new Way();
        way1.addNodeId(1);
        way1.addNodeId(2);

        LinePath lp = new LinePath(way1, OSMType.BUILDING, nodes1, true);

        return lp;
    }

    @Test
    public void fetchHighwayData() {
        assertEquals(appController.fetchHighways().size(), 0);
    }

    @Test
    public void save_fetchRouteData() {
        appController.saveRouteData(route);
        assertEquals(appController.fetchRouteData(), route);
    }

    @Test
    public void save_fetch_clearRouteInfoData() {
        appController.saveRouteDirections(routeInfo);
        assertEquals(appController.fetchRouteDirections(), routeInfo);
        appController.clearRouteInfoData();
        routeInfo.clear();
        assertEquals(appController.fetchRouteDirections(), routeInfo);
    }

    @Test
    public void saveAddressData() {
        Address address = new Address("Farum", "21", "3520", "2", 21, 22, 26);
        appController.saveAddressData((long) 1, address);


    }

    @Test
    public void save_fetchRelationData() {
        appController.saveRelationData(relation);
        assertEquals(appController.fetchRelations().size(), 1);
    }

    @Test
    public void save_fetchBoundsData() {
        Bounds bounds = new Bounds(10, 1, 5, 2);
        appController.saveBoundsData(bounds);
        assertEquals(appController.fetchBoundsData().getMaxLat(), bounds.getMaxLat());
    }

    @Test
    public void save_getNodeData() {
        Node node = new Node(2, (float) 55.6388541, (float) 12.6195888);
        appController.saveNodeData(2, node);
        assertEquals(appController.fetchNodeData(2), node);
    }

    @Test
    public void save_getWayData() {
        Way way2 = new Way();
        List<Way> wayList = new ArrayList<>();
        wayList.add(way2);

        appController.saveWayData(way2);
        assertEquals(appController.fetchAllWays(), wayList);
    }

    @Test
    public void removeWayFrom_get_saveNodeToData() {
        assertEquals(appController.getNodeTo(OSMType.HIGHWAY), null);
        appController.saveNodeToData(OSMType.HIGHWAY, node1, way1);
        assertEquals(appController.getNodeTo(OSMType.HIGHWAY).get(node1), way1);
        appController.removeWayFromNodeTo(OSMType.HIGHWAY, node1);
        assertEquals(appController.getNodeTo(OSMType.HIGHWAY).get(node1), null);

    }


    @Test
    public void save_fetchCoastlines() {

        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(new Way(), OSMType.COASTLINE, new HashMap<>(), true);
        linePaths.add(linePath);
        appController.saveCoastlines(linePaths);
        assertEquals(appController.fetchCoastlines(), linePaths);
        linePaths.clear();
    }

    @Test
    public void setup_fetchRectData() {
        appController.setupRect();
        assertNotNull(appController.fetchRectData());

    }

    @Test
    public void save_fetchLinePathData(){
        appController.clearLinePathData();
        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(new Way(), OSMType.COASTLINE, new HashMap<>(), true);
        LinePath linePath2 = new LinePath(new Way(), OSMType.BUILDING, new HashMap<>(), true);
        linePaths.add(linePath);
        appController.saveLinePathData(OSMType.COASTLINE,linePath);
        assertEquals(appController.fetchCoastlines(), linePaths);
        appController.saveLinePathData(OSMType.BUILDING,linePath2);
        assertNotNull(appController.fetchLinePathData());

        appController.clearLinePathData();
        assertNull(appController.fetchLinePathData().get(0));
        linePaths.clear();
    }

    @Test
    public void save_fetchKDTree(){
        List<LinePath> linePaths = new ArrayList<>();
        LinePath linePath = new LinePath(new Way(), OSMType.BUILDING, new HashMap<>(), true);
        linePaths.add(linePath);
        appController.saveKDTree(OSMType.BUILDING, linePaths);
        
        assertNotNull(appController.fetchKDTree(OSMType.BUILDING));

    }
    
}