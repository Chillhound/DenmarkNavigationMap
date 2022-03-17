package bfst20.logic.routing;

import bfst20.logic.entities.Node;

import java.io.Serializable;
import java.util.*;

public class Graph implements Serializable{
    private Map<Node, List<Edge>> adj;
    private List<Edge> edges;
    private List<Node> nodes;
    private int nodeCount;


    public Graph(List<Node> nodes) {
        this.nodeCount = nodes.size();
        edges = new ArrayList<>();
        adj = new HashMap<>();
        this.nodes = nodes;

        for (int i = 0; i < nodeCount; i++) {
            adj.put(nodes.get(i), new ArrayList<>());
        }

    }

    public void sortEdges() {
        edges.sort(Comparator.comparing(Edge::getStreet));
    }

    public int nodeCount() {
        return nodeCount;
    } 

    public List<Edge> getEdges() {
        return edges;
    }

    public List<Node> getNodes() {
        return nodes;
    }

    public void addEdge(Edge edge) {
        edges.add(edge);
        if (!adj.get(edge.getSource()).contains(edge)) adj.get(edge.getSource()).add(edge);
        if (!adj.get(edge.getTarget()).contains(edge)) adj.get(edge.getTarget()).add(edge);

    }

    public Iterable<Edge> adj(Node node) {
        return adj.get(node);
    }

}
