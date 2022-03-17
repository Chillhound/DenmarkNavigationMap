package bfst20.data;

import bfst20.logic.misc.OSMType;
import bfst20.logic.entities.Node;
import bfst20.logic.entities.Way;
import bfst20.logic.entities.LinePath;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LinePathData {
    private Map<OSMType, List<LinePath>> linePaths;
    private Map<OSMType, Map<Node, Way>> nodeTo;
    private static LinePathData linePathData;
    private static boolean isLoaded = false;
    private List<LinePath> coastlines;
    private List<LinePath> motorways;
    private List<LinePath> highways;

    private LinePathData() {
        linePaths = new HashMap<>();
        nodeTo = new HashMap<>();
        highways = new ArrayList<>();
        coastlines = new ArrayList<>();
        motorways = new ArrayList<>();
    }

    public static LinePathData getInstance() {
        if (!isLoaded) {
            isLoaded = true;
            linePathData = new LinePathData();
        }

        return linePathData;
    }

    public void saveHighways(List<LinePath> highways) {
        this.highways = highways;
    }

    public List<LinePath> getHighways() {
        return this.highways;
    }

    public Map<OSMType, List<LinePath>> getLinePaths() {
        return linePaths;
    }

    public void saveLinePath(OSMType type, LinePath linePath) {
        if (linePaths.get(type) == null) linePaths.put(type, new ArrayList<>());
        if (type == type.COASTLINE) coastlines.add(linePath);
        else if (type == type.MOTORWAY) motorways.add(linePath);

        else linePaths.get(type).add(linePath);
    }

    public List<LinePath> getCoastlines() {
        return coastlines;
    }


    public void saveCoastlines(List<LinePath> coastlines) {
        this.coastlines = coastlines;
    }

    public List<LinePath> getMotorways() {
        return motorways;
    }


    public void saveLinePaths(Map<OSMType, List<LinePath>> linePaths) {
        this.linePaths = linePaths;
    }

    public void addNodeTo(OSMType type, Node node, Way way) {
        if (nodeTo.get(type) == null) nodeTo.put(type, new HashMap<>());


        nodeTo.get(type).put(node, way);
    }

    public Map<Node, Way> getNodeTo(OSMType type) {
        return nodeTo.get(type);
    }

    public Way removeWayFromNodeTo(OSMType type, Node node) {
        if (nodeTo.get(type) == null) return null;

        return nodeTo.get(type).remove(node);
    }

    public void clearData() {
        linePaths = new HashMap<>();
        nodeTo = new HashMap<>();
        highways = new ArrayList<>();

        System.gc();
    }

    public void clearCoastlines() {
        coastlines = new ArrayList<>();
    }

    public void clearMotorways(){
        motorways = new ArrayList<>();
    }

    public void saveSingleCoastLine(LinePath linePath) {
        if (coastlines == null) coastlines = new ArrayList<>();

        coastlines.add(linePath);
    }

}
