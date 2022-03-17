package bfst20.data;

import bfst20.logic.entities.Node;
import bfst20.logic.misc.OSMType;
import bfst20.logic.routing.Edge;
import bfst20.logic.routing.Graph;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class RoutingDataTest {
    public static Graph graph;
    
    public static List<Edge> route;
    
    public static RoutingData routingData;
    public static Map<String, Double> routeInfo;
  
    public static Node node1;
    public static Node node2;
    public static Edge edge;
    
    @BeforeAll
    public static void setup(){
        node1 = new Node(1, (float) 55.6388937, (float) 12.6195664);
        node2 = new Node(2, (float) 55.6388541, (float) 12.6195888);
        edge = new Edge(OSMType.MOTORWAY,node1,node2,100,"Farstrupvej",50,false);
        routingData = RoutingData.getInstance();
        route = new ArrayList<>();
        routeInfo = new HashMap<>();

        route.add(edge);
        routeInfo.put("Farstrupvej",100.0);
    }

    @Test
    void getInstance() {
    assertEquals(routingData.getInstance(),routingData);
    }

    //Combined two methods, not to create excessive test.
    @Test
    void get_saveGraph() {
        routingData.saveGraph(graph);
        assertEquals(routingData.getGraph(),graph);
    }

    @Test
    void get_saveRoute() {
        routingData.saveRoute(route);
        assertEquals(routingData.getRoute(),route);
    }

    @Test
    void saveRouteInfo() {
        routingData.saveRouteDirections(routeInfo);
        assertEquals(routingData.getRouteDirections(),routeInfo);
    }


}