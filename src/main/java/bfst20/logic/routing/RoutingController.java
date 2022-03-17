package bfst20.logic.routing;

import bfst20.logic.AppController;
import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.Address;
import bfst20.logic.entities.LinePath;
import bfst20.logic.entities.Node;
import bfst20.logic.entities.Way;
import bfst20.logic.misc.Vehicle;

import java.util.*;

public class RoutingController {
    private AppController appController;

    public RoutingController(AppController appController) {
        this.appController = appController;
    }

    public void buildRoutingGraph() {
        List<LinePath> highways = appController.fetchHighways();
        List<Node> highwayNodes = new ArrayList<>();

        for (LinePath lp : highways) {
            if (lp.getWay() == null) continue;
            highwayNodes.addAll(lp.getWay().getNodes());
        }

        Graph graph = new Graph(new ArrayList<>(highwayNodes));
        generateGraphEdges(highways, graph);
        graph.sortEdges();

        appController.saveGraphData(graph);

        highwayNodes = null;
        graph = null;
        System.gc();
    }

    private void generateGraphEdges(List<LinePath> highWays, Graph graph) {
        for (LinePath linePath : highWays) {
            Way way = linePath.getWay();

            if (way != null) {
                OSMType type = way.getOSMType();
                for (int i = 1; i < way.getNodes().size(); i++) {
                    Node sourceNode = way.getNodes().get(i - 1);
                    Node targetNode = way.getNodes().get(i);

                    double length = haversine(sourceNode, targetNode);
                    Edge edge = new Edge(type, sourceNode, targetNode, length, way.getName(), way.getMaxSpeed(),
                            way.isOneWay());

                    graph.addEdge(edge);
                }
            }
        }
    }

    public double calculateShortestRoute(Graph graph, List<Edge> edges, Address srcAddress, Address trgAddress, Vehicle vehicle) {
        Node srcNode = findClosestNodeTo(srcAddress, edges, vehicle);
        Node trgNode = findClosestNodeTo(trgAddress, edges, vehicle);

        Dijkstra dijkstra = new Dijkstra(graph, srcNode, trgNode, vehicle);
        
        if (dijkstra.distTo(trgNode) != Double.POSITIVE_INFINITY) {
            List<Edge> route = new ArrayList<>();

            if (dijkstra.getEdgeTo().size() == 1) route.addAll(dijkstra.getEdgeTo().values());
            else route.addAll(extractRouteInfo(dijkstra.getEdgeTo(), srcNode, trgNode));

            appController.saveRouteData(route);
        }

        double dist = dijkstra.distTo(trgNode);
        dijkstra.clearData();
        return dist;
    }

    private Node findClosestNodeTo(Address address, List<Edge> edges, Vehicle vehicle) {
        List<Edge> closestEdges = new ArrayList<>();
        int addressIndex = binarySearch(edges, address.getStreet());
        int searchInterval = 5000;

        for (int i = addressIndex - searchInterval; i < addressIndex + searchInterval; i++) {
            if (i >= 0 && i < edges.size() && edges.get(i).getStreet().equals(address.getStreet())) {
                closestEdges.add(edges.get(i));
            }
        }
        Node closestNode = calculateDistanceBetween(address, closestEdges, vehicle);

        return closestNode;
    }

    private Node calculateDistanceBetween(Address address, List<Edge> closestEdges, Vehicle vehicle) {
        float shortestDistance = Float.POSITIVE_INFINITY;
        Node closestNode = null;

        for (Edge e : closestEdges) {
            float distance = (float) Math.sqrt(Math.pow(e.getTarget().getLatitude() - address.getLat(), 2)
                    + Math.pow(e.getTarget().getLongitude() - address.getLon(), 2));

            if (distance < shortestDistance && e.isVehicleAllowed(vehicle)) {
                closestNode = e.getTarget();
                shortestDistance = distance;
            }
        }

        return closestNode;
    }


    private List<Edge> extractRouteInfo(Map<Node, Edge> dijkstraEdges, Node source, Node target) {
        Map<String, Double> routeDirections = new LinkedHashMap<>();
        List<Edge> edges = new ArrayList<>();
        Edge edge = dijkstraEdges.get(target);
        long id = 0;

        while (id != source.getId()) {
            edge = dijkstraEdges.get(edge.getTarget());
            long tempID = edge.getTarget().getId();

            // Follow either target or source node
            edge = id == tempID ? dijkstraEdges.get(edge.getTarget()) : dijkstraEdges.get(edge.getSource());
            id = id == tempID ? edge.getSource().getId() : edge.getTarget().getId();

            edges.add(edge);
            collectRouteDirections(routeDirections, edge);
        }

        appController.saveRouteDirections(routeDirections);
        return edges;
    }

    private void collectRouteDirections(Map<String, Double> routeInfo, Edge edge) {
        if (!routeInfo.containsKey(edge.getStreet())) {
            routeInfo.put(edge.getStreet(), (double) Math.round(edge.getLength() * 100) / 100);
        } else {
            routeInfo.put(edge.getStreet(), (double) Math.round((routeInfo.get(edge.getStreet()) + edge.getLength()) * 100) / 100);
        }
    }

    private int binarySearch(List<Edge> list, String address) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Edge midElement = list.get(mid);
            String midStreet = midElement.getStreet();

            if (midStreet.compareTo(address) < 0) {
                low = mid + 1;
            } else if (midStreet.compareTo(address) > 0) {
                high = mid - 1;
            } else {
                return low;
            }
        }

        return 0;
    }

    //This formula is used because the earth is a sphere and not flat
    private double haversine(Node source, Node target) {
        double sourceLat = source.getLatitude();
        double sourceLong = source.getLongitude();
        double targetLat = target.getLatitude();
        double targetLong = target.getLongitude();

        double earthRadius = 6371.0;
        double dLat = Math.toRadians(targetLat - sourceLat);
        double dLng = Math.toRadians(targetLong - sourceLong);

        double sindLat = Math.sin(dLat / 2);
        double sindLng = Math.sin(dLng / 2);
        double a = Math.pow(sindLat, 2)
                + Math.pow(sindLng, 2) * Math.cos(Math.toRadians(sourceLat)) * Math.cos(Math.toRadians(targetLat));

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        double dist = earthRadius * c;

        return dist;
    }
}

