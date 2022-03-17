package bfst20.logic.routing;

import bfst20.logic.misc.OSMType;

import java.io.Serializable;

import bfst20.logic.entities.Node;
import bfst20.logic.misc.Vehicle;

public class Edge implements Serializable {
    private OSMType highwayType;
    private boolean isOneWay;
    private double length;
    private String street;
    private int maxSpeed;
    private Node source;
    private Node target;


    public Edge(OSMType highwayType, Node source, Node target, double length, String street, int maxSpeed, boolean isOneWay) {
        this.maxSpeed = maxSpeed != 0 ? maxSpeed : OSMType.getMaxSpeed(highwayType);
        this.highwayType = highwayType;
        this.isOneWay = isOneWay;
        this.source = source;
        this.target = target;
        this.length = length;

        this.street = street == null ? "ååååå" : street.intern();
    }

    public boolean isOneWay(Vehicle vehicle) {
        if (vehicle == Vehicle.CAR) return isOneWay;

        return false;
    }

    public boolean isVehicleAllowed(Vehicle vehicle) {
        if (vehicle == Vehicle.BICYCLE && highwayType == OSMType.MOTORWAY || vehicle == Vehicle.WALK && highwayType == OSMType.MOTORWAY) {
            return false;
        } else if (     vehicle == Vehicle.CAR
                    &&  highwayType == OSMType.FOOTWAY
                    ||  vehicle == Vehicle.CAR
                    &&  highwayType == OSMType.PATH
                    ||  vehicle == Vehicle.CAR
                    &&  highwayType == OSMType.CYCLEWAY) {
            return false;
        }

        return true;
    }

    public String getStreet() {
        return street;
    }

    public Node getSource() {
        return source;
    }

    public Node getTarget() {
        return target;
    }

    public int getMaxSpeed() {
        return maxSpeed;
    }

    public double getLength() {
        return length;
    }


}
