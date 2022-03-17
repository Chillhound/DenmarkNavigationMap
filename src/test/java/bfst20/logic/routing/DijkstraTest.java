package bfst20.logic.routing;

import bfst20.logic.entities.Node;
import bfst20.logic.misc.OSMType;
import bfst20.logic.misc.Vehicle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class DijkstraTest {
    private static Graph graph;
    private static List<Node> nodes = new ArrayList<>();
    private static Node node1 = new Node(1, 2, 3);
    private static Node node2 = new Node(3, 4, 5);
    private static Node node3 = new Node(4, 5, 6);
    private static Node node4 = new Node(7, 8, 9);
    private static Node node5 = new Node(10, 11, 12);
    private static Node node6 = new Node(13, 14, 15);
    private static Node node7 = new Node(16, 17, 18);
    private static Node node8 = new Node(19, 20, 21);
    private static Edge edge1 = new Edge(OSMType.PRIMARY, node1, node2, 2, "Gedevasevej", 50, false);
    private static Edge edge2 = new Edge(OSMType.SECONDARY, node1, node3, 5, "Dyrevænget", 75, false);
    private static Edge edge3 = new Edge(OSMType.TERTIARY, node2, node4, 6, "Fuglsangpark", 60, false);
    private static Edge edge4 = new Edge(OSMType.MOTORWAY, node5, node6, 7, "E20", 130, false);
    private static Edge edge5 = new Edge(OSMType.TERTIARY, node1, node4, 11, "Farum Hovedgade", 50, true);
    private static Edge edge6 = new Edge(OSMType.SERVICE, node7, node8, 12, "Skovlunde Byvej", 80, false);

    @BeforeAll
    static void setup() {
        Collections.addAll(nodes, node1, node2, node3, node4, node5, node6, node7, node8);
        graph = new Graph(nodes);

        graph.addEdge(edge1);
        graph.addEdge(edge2);
        graph.addEdge(edge3);
        graph.addEdge(edge4);
        graph.addEdge(edge5);
        graph.addEdge(edge6);
    }

    @Test
    void findShortestPath() {
        Dijkstra dijkstra = new Dijkstra(graph, node1, node4, Vehicle.CAR);

        assertEquals(0.04, dijkstra.distTo(node2));
        assertEquals(0.14, dijkstra.distTo(node4));

        Dijkstra dijkstra1 = new Dijkstra(graph, node7, node8, Vehicle.BICYCLE);

        assertEquals(12, dijkstra1.distTo(node8));

    }

    @Test
    void getEdgeTo() {
        Dijkstra dijkstra = new Dijkstra(graph, node1, node4, Vehicle.WALK);

        assertEquals(3, dijkstra.getEdgeTo().size());
    }

    @Test
    void distTo() {
        Dijkstra dijkstra = new Dijkstra(graph, node1, node8, Vehicle.BICYCLE);

        assertEquals(Double.POSITIVE_INFINITY, dijkstra.distTo(node8));
        assertEquals(5, dijkstra.distTo(node3));
    }

    @Test
    void hasPathTo() {
        Dijkstra dijkstra = new Dijkstra(graph, node2, node4, Vehicle.CAR);

        assertEquals(true, dijkstra.hasPathTo(node4));
        assertEquals(false, dijkstra.hasPathTo(node8));
    }
}