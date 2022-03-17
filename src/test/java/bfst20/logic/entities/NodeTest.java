package bfst20.logic.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class NodeTest {
    public static Node node1;
    public static Node node2;

    @BeforeAll
    public static void setup(){
       node1 = new Node(1,10,20);
       node2 = new Node(5,15); //constructor without id field
    }
    @Test
    public void getId() {
        assertEquals(node1.getId(), 1);
    }
    @Test
    public void getLatitude() {
        assertEquals(node1.getLatitude(), 10);
        assertEquals(node2.getLatitude(), 5);
    }
    
    @Test
    public void getLongitude() {
        assertEquals(node1.getLongitude(), 20);
        assertEquals(node2.getLongitude(), 15);
    }

    @Test
    public void setDistTo() {
        node1.setDistTo(30);
        node2.setDistTo(60);
        assertEquals(node1.getDistTo(), 30);
        assertEquals(node2.getDistTo(), 60);
    }

    @Test
    public void compareTo(){
        node1.setDistTo(30);
        node2.setDistTo(60);
        assertEquals(node1.compareTo(node2), -1);
        assertEquals(node2.compareTo(node1), 1);
    }

}

