package bfst20.logic.routing;

import bfst20.logic.entities.Node;
import bfst20.logic.misc.OSMType;
import bfst20.logic.misc.Vehicle;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EdgeTest {

    private static Edge edge;
    private static Node source = new Node(1, 2, 3);
    private static Node target = new Node(3, 4, 5);
    private static double length = 2;
    private static String street = "Fuglsangpark";
    private static int maxSpeed = 100;
    private static boolean isOneWay = true;
    private static Vehicle vehicle = Vehicle.CAR;

    @BeforeAll
    static void setup() {
        edge = new Edge(OSMType.MOTORWAY, source, target, length, street, maxSpeed, isOneWay);
    }

    @Test
    void isOneWay() {

        assertEquals(true, edge.isOneWay(vehicle));
        assertEquals(false, edge.isOneWay(Vehicle.BICYCLE));
    }

    @Test
    void isVehicleAllowed() {

        assertEquals(false, edge.isVehicleAllowed(Vehicle.BICYCLE));
        assertEquals(true, edge.isVehicleAllowed(vehicle));
    }

    @Test
    void getStreet() {
        assertEquals(street, edge.getStreet());
    }

    @Test
    void getSource() {
        assertEquals(source, edge.getSource());
    }

    @Test
    void getTarget() {
        assertEquals(target, edge.getTarget());
    }

    @Test
    void getMaxSpeed() {
        assertEquals(maxSpeed, edge.getMaxSpeed());
    }

    @Test
    void getLength() {
        assertEquals(length, edge.getLength());
    }
}