package bfst20.logic.entities;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import bfst20.logic.misc.OSMType;

public class LinePath implements Serializable {
    private float minY, minX, maxY, maxX, centerLatitude, centerLongitude;
    private boolean multipolygon;
    private OSMType OSMType;
    private float[] coords;
    private Bounds bounds;
    private boolean fill;
    private String name;
    private long wayId;
    private Way way;

    public LinePath(float maxLat, float maxLon, float minLat, float minLon) {
        this.bounds = new Bounds(maxLat, minLat, maxLon, minLon);
        minY = minLon;
        maxY = maxLon;
        minX = minLat;
        maxX = maxLat;
        centerLatitude = (maxX - minX) / 2 + minX;
        centerLongitude = (maxY - minY) / 2 + minY;
    }

    public LinePath(Way way, OSMType OSMType, Map<Long, Node> OSMNodes, Boolean fill) {
        name = way.getName();
        wayId = way.getId();
        this.way = way;
        this.fill = fill;
        this.OSMType = OSMType;


        calculateMinMaxCoordinates(OSMNodes, way);
        calculateCenterCoordinates();
    }

    public void calculateMinMaxCoordinates(Map<Long, Node> OSMNodes, Way way){

        List<Long> nodeIds = way.getNodeIds();

        minY = Float.POSITIVE_INFINITY;
        minX = Float.POSITIVE_INFINITY;
        maxY = Float.NEGATIVE_INFINITY;
        maxX = Float.NEGATIVE_INFINITY;

        coords = new float[nodeIds.size() * 2];
        for (int i = 0; i < nodeIds.size(); i++) {


            coords[i * 2] = OSMNodes.get(nodeIds.get(i)).getLongitude();
            coords[i * 2 + 1] = OSMNodes.get(nodeIds.get(i)).getLatitude();

            if (minX > coords[i * 2 + 1]) minX = coords[i * 2 + 1];
            if (minY > coords[i * 2]) minY = coords[i * 2];
            if (maxX < coords[i * 2 + 1]) maxX = coords[i * 2 + 1];
            if (maxY < coords[i * 2]) maxY = coords[i * 2];

        }
    }

    public void calculateCenterCoordinates(){
        centerLatitude = (maxX - minX) / 2 + minX;
        centerLongitude = (maxY - minY) / 2 + minY;
    }

    public boolean isMultipolygon() {
        return multipolygon;
    }

    public void setMultipolygon(boolean multipolygon) {
        this.multipolygon = multipolygon;
    }

    public Bounds getBounds() {
        return this.bounds;
    }

    public float getCenterLatitude() {
        return centerLatitude;
    }

    public float getCenterLongitude() {
        return centerLongitude;
    }

    public float getMaxX() {
        return maxX;
    }

    public float getMaxY() {
        return maxY;
    }

    public float getMinX() {
        return minX;
    }

    public float getMinY() {
        return minY;
    }

    public float[] getCoords() {
        return coords;
    }

    public boolean getFill() {
        return this.fill;
    }

    public String getName() {
        return name;
    }

    public OSMType getOSMType() {
        return OSMType;
    }

    public Way getWay() {
        return this.way;
    }

    public Long getWayId() {
        return wayId;
    }

    public void removeWay() {
        way = null;
        wayId = 0;
    }


}