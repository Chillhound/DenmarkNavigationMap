package bfst20.data;

import bfst20.logic.routing.Edge;
import bfst20.logic.routing.Graph;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RoutingData {
    private static boolean isLoaded = false;
    private static RoutingData routingData;
    private Map<String, Double> routeInfo;
    private List<Edge> route;
    private Graph graph;


    private RoutingData() {
    }

    public static RoutingData getInstance() {
        if (!isLoaded) {
            isLoaded = true;
            routingData = new RoutingData();
        }
        return routingData;
    }

    public void saveGraph(Graph graph) {
        this.graph = graph;
    }

    public Graph getGraph() {
        return graph;
    }

    public void saveRoute(List<Edge> route) {
        this.route = route;
    }

    public List<Edge> getRoute() {
        return route;
    }

    public void saveRouteDirections(Map<String, Double> routeInfo) {
        this.routeInfo = routeInfo;
    }

    public Map<String, Double> getRouteDirections() {
        return routeInfo;
    }

    public void clearData() {
        route = new ArrayList<>();
        routeInfo = new HashMap<>();

        System.gc();
    }
}
