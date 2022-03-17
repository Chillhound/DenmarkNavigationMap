package bfst20.logic.routing;

import bfst20.logic.entities.Node;
import bfst20.logic.misc.OSMType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class GraphTest {
    private static Graph graph;
    private static List<Node> nodes = new ArrayList<>();
    private static Node node1 = new Node(1, 2, 3);
    private static Node node2 = new Node(3, 4, 5);
    private static Edge edge1 = new Edge(OSMType.PRIMARY, node1, node2, 3, "Gedevasevej", 75, false);
    private static Edge edge2 = new Edge(OSMType.PRIMARY, node1, node2, 5, "Dyrevænget", 75, false);


    @BeforeAll
    static void setup() {
        Collections.addAll(nodes, node1, node2);
        graph = new Graph(nodes);

        graph.addEdge(edge1);
        graph.addEdge(edge2);
    }

    @Test
    void sortEdges() {
        graph.sortEdges();

        assertEquals(edge2, graph.getEdges().get(0));
    }

    @Test
    void nodeCount() {
        assertEquals(2, graph.nodeCount());
    }

    @Test
    void getNodes() {
        assertEquals(2, graph.getNodes().size());
    }

    @Test
    void getEdges() {
        assertEquals(2, graph.getEdges().size());
    }

    @Test
    void addEdge() {
        assertEquals(2, graph.getEdges().size());
    }

    @Test
    void adj() {
        List<Edge> list = (List) graph.adj(node1);
        assertEquals(2, list.size());

    }
}
