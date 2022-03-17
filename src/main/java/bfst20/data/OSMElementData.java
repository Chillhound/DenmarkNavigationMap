package bfst20.data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import bfst20.logic.entities.*;


public class OSMElementData {
    private static OSMElementData OSMElementData;
    private static boolean isLoaded = false;
    private List<Relation> OSMRelations;
    private Map<Long, Node> nodeMap;
    private List<Way> OSMWays;
    private Bounds bounds;


    private OSMElementData() {
        OSMWays = new ArrayList<>();
        nodeMap = new HashMap<>();
        OSMRelations = new ArrayList<>();
    }

    public static OSMElementData getInstance() {
        if (!isLoaded) {
            isLoaded = true;
            OSMElementData = new OSMElementData();
        }
        return OSMElementData;
    }

    public void saveBounds(Bounds bounds) {
        this.bounds = new Bounds(bounds.getMaxLat(), bounds.getMinLat(), bounds.getMaxLon(), bounds.getMinLon());
    }

    public Bounds getBounds() {
        return bounds;
    }

    public Node getNode(long id) {
        return nodeMap.get(id);
    }

    public Map<Long, Node> getNodes() {
        return nodeMap;
    }

    public void addToNodeMap(long id, Node node) {
        nodeMap.put(id, node);
    }

    public void saveWay(Way way) {
        OSMWays.add(way);
    }

    public List<Way> getWays() {
        return OSMWays;
    }

    public void saveRelation(Relation relation) {
        OSMRelations.add(relation);
    }

    public List<Relation> getRelations() {
        return OSMRelations;
    }

    public void clearNodeData() {
        OSMRelations = new ArrayList<>();
        OSMWays = new ArrayList<>();
        nodeMap = new HashMap<>();

        System.gc();
    }
}