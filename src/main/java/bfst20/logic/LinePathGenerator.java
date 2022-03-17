package bfst20.logic;

import java.util.*;

import bfst20.logic.entities.Node;
import bfst20.logic.entities.Relation;
import bfst20.logic.entities.Way;
import bfst20.logic.entities.LinePath;
import bfst20.logic.misc.OSMType;

public class LinePathGenerator {
    private static LinePathGenerator linePathGenerator;
    private static boolean loaded = false;
    private AppController appController;
    private List<Relation> relations;
    private Map<Long, Node> nodes;
    private List<Way> ways;

    private LinePathGenerator(AppController appController) {
        this.appController = appController;
    }

    public static LinePathGenerator getInstance(AppController appController) {
        if (!loaded) {
            loaded = true;
            linePathGenerator = new LinePathGenerator(appController);
        }

        return linePathGenerator;
    }

    public void convertWaysToLinePaths(List<Way> ways, Map<Long, Node> nodes) {
        this.nodes = nodes;
        this.ways = ways;
        for (Way way : ways) {
            if (way.getOSMType() == OSMType.COASTLINE || way.getOSMType() == null) continue;

            LinePath linePath = createLinePath(way);

            OSMType type = linePath.getOSMType();

            if (type != OSMType.PLACE) {
                appController.saveLinePathData(type, linePath);

            }
        }
    }

    private LinePath createLinePath(Way way) {
        OSMType type = OSMType.UNKNOWN;

        try {
            type = way.getOSMType();
        } catch (Exception e) {
            //This catch is here to check if the current way type exists in the Type enum, if it does, that will be used,
            //If it dosen't this will throw, and the program will use Type.UNKNOWN
        }
        Boolean fill = OSMType.getFill(type);

        return new LinePath(way, type, nodes, fill);
    }

    public void convertRelationsToLinePaths(List<Relation> relations) {
        this.relations = relations;
        for (Relation relation : this.relations) {

            if (relation.getOSMType() == OSMType.FOREST) connectWays(relation, OSMType.FOREST);

            else if (relation.getOSMType() == OSMType.FARMLAND) connectWays(relation, OSMType.FARMLAND);

            else if (relation.getName() != null && relation.getName().startsWith("Region ")) {

                connectWays(relation, OSMType.COASTLINE);

            } else if (relation.getOSMType() == OSMType.BUILDING) connectMultipolygon(relation, OSMType.BUILDING);

            else if (relation.getOSMType() == OSMType.MEADOW) connectMultipolygon(relation, OSMType.MEADOW);

            else if (relation.getOSMType() == OSMType.HEATH) connectMultipolygon(relation, OSMType.HEATH);
        }

        OSMType[] types = OSMType.relations();

        for (OSMType type : types) {
            if (appController.getNodeTo(type) != null) {
                addRelation(type, appController.getNodeTo(type));
            }
        }
    }

    //This connect different ways in relations
    private void connectWays(Relation relation, OSMType OSMType) {
        Collections.sort(relation.getMembers());

        for (long entry : relation.getMembers()) {

            Way way = (binarySearch(ways, entry));
            if (way == null) continue;

            if (relation.isMultipolygon()) {
                way.setMultipolygon(true);
            }

            Way before = removeWayBefore(way, OSMType);
            Way after = removeWayAfter(way, OSMType);

            way = merge(merge(before, way), after);

            appController.saveNodeToData(OSMType, nodes.get(way.getFirstNodeId()), way);
            appController.saveNodeToData(OSMType, nodes.get(way.getLastNodeId()), way);
        }
    }

    private Way removeWayAfter(Way way, OSMType OSMType) {
        Node node = nodes.get(way.getLastNodeId());
        return getWay(OSMType, node);
    }

    private Way removeWayBefore(Way way, OSMType OSMType) {
        Node node = nodes.get(way.getFirstNodeId());
        return getWay(OSMType, node);
    }

    private Way getWay(OSMType OSMType, Node node) {
        Way way = appController.removeWayFromNodeTo(OSMType, node);
        if (way != null) {
            Node firstNode = nodes.get(way.getFirstNodeId());
            Node lastNode = nodes.get(way.getLastNodeId());
            appController.removeWayFromNodeTo(OSMType, firstNode);
            appController.removeWayFromNodeTo(OSMType, lastNode);

        }

        return way;
    }

    private Way combineWays(Way first, Way second) {
        if (first == null) return second;
        if (second == null) return first;

        Way way = new Way();
        way.addAllNodeIds(first);
        way.addAllNodeIds(second);

        return way;
    }

    private Way merge(Way before, Way after) {
        if (before == null) return after;
        if (after == null) return before;

        Way way = new Way();
        // Why do we need this? Seems to do the same without it
        if (before.getFirstNodeId() == after.getFirstNodeId()) {
            way.addAllNodeIds(before);

            Collections.reverse(way.getNodeIds());
            way.getNodeIds().remove(way.getNodeIds().size() - 1);
            way.addAllNodeIds(after);

        } else if (before.getFirstNodeId() == after.getLastNodeId()) {

            addWayToMerge(way, after, before);

        } else if (before.getLastNodeId() == after.getFirstNodeId()) {

            addWayToMerge(way, before, after);
        }

        // Why do we need this? Seems to do the same without it
        else if (before.getLastNodeId() == after.getLastNodeId()) {
            Way tmp = new Way(after);

            Collections.reverse(tmp.getNodeIds());
            way.addAllNodeIds(before);
            way.getNodeIds().remove(way.getNodeIds().size() - 1);
            way.addAllNodeIds(tmp);
        } else {
            throw new IllegalArgumentException("Cannot merge unconnected OSMWays");
        }

        return way;
    }

    //Order of before and after depends on the context
    private void addWayToMerge(Way way, Way before, Way after) {
        way.addAllNodeIds(before);
        way.getNodeIds().remove(way.getNodeIds().size() - 1);
        way.addAllNodeIds(after);
    }

    private void connectMultipolygon(Relation relation, OSMType osmType) {
        if (!relation.isMultipolygon()) return;
        Collections.sort(relation.getMembers());

        Way way = null;

        for (long entry : relation.getMembers()) {

            if (way == null) {
                way = (binarySearch(ways, entry));
            } else {
                Way newWay = (binarySearch(ways, entry));
                way = combineWays(way, newWay);
            }
        }

        way.setMultipolygon(true);

        appController.saveNodeToData(osmType, nodes.get(way.getFirstNodeId()), way);
        appController.saveNodeToData(osmType, nodes.get(way.getLastNodeId()), way);
    }


    private void addRelation(OSMType OSMType, Map<Node, Way> nodeTo) {
        for (Map.Entry<Node, Way> entry : nodeTo.entrySet()) {
            if (entry.getKey() == nodes.get(entry.getValue().getLastNodeId())) {

                LinePath path = new LinePath(entry.getValue(), OSMType, nodes, true);

                if (entry.getValue().isMultipolygon()) {
                    path.setMultipolygon(true);
                }

                appController.saveLinePathData(OSMType, path);
            }
        }
    }




    private Way binarySearch(List<Way> list, long id) {
        int low = 0;
        int high = list.size() - 1;

        while (low <= high) {
            int mid = (low + high) / 2;
            Way midElement = list.get(mid);
            long midId = midElement.getId();

            if (midId < id) {
                low = mid + 1;
            } else if (midId > id) {
                high = mid - 1;
            } else {
                return midElement;
            }
        }
        return null;
    }

    public void clearData() {
        nodes = new HashMap<>();
        ways = new ArrayList<>();
        relations = new ArrayList<>();

        loaded = false;
        System.gc();
    }
}