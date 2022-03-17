package bfst20.logic.entities;

import bfst20.logic.misc.OSMType;
import bfst20.logic.misc.OSMElement;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Way implements OSMElement, Serializable {
    private boolean multipolygon;
    private List<Long> nodeIds;
    private boolean isOneWay;
    private List<Node> nodes;
    private OSMType OSMType;
    private int maxSpeed;
    private String name;
    private long id;

    public Way() {
        nodeIds = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    public Way(long id) {
        this.id = id;
        nodeIds = new ArrayList<>();
        nodes = new ArrayList<>();
    }

    //Used to make a temp copy of a way in LinePathGenerator.
    public Way(Way way) {
        this.id = way.getId();
        this.nodeIds = new ArrayList<>(way.getNodeIds());
        nodes = way.getNodes();
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public void setMaxSpeed(int maxSpeed) {
        this.maxSpeed = maxSpeed;
    }

    public boolean isOneWay() {
        return isOneWay;
    }

    public void setOneWay(boolean oneWay) {
        this.isOneWay = oneWay;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setMultipolygon(boolean multipolygon) {
        this.multipolygon = multipolygon;
    }

    public boolean isMultipolygon() {
        return multipolygon;
    }

    public void setOSMType(OSMType type) {
        this.OSMType = type;
    }

    public OSMType getOSMType() {
        return OSMType;
    }
 
   
    public List<Long> getNodeIds() {
        return nodeIds;
    }

    public long getId() {
        return id;
    }

  
    public void addNodeId(long id) {
        nodeIds.add(id);
    }

    public long getFirstNodeId() {
        return nodeIds.get(0);
    }

    public long getLastNodeId() {
        return nodeIds.get(nodeIds.size() - 1);
    }

    public void addAllNodeIds(Way way) {
        nodeIds.addAll(way.getNodeIds());
    }

    public void addNode(Node node) {
        nodes.add(node);
    }

    public List<Node> getNodes() {
        return nodes;
    }

}