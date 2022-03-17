package bfst20.logic.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class BoundsTest{
    public static Bounds bounds;

    @BeforeAll
    public static void setup(){
        bounds = new Bounds(10,1,5,2);
    }

    @Test
    public void getMaxLat(){
        assertEquals(bounds.getMaxLat(),10);            
    }

    @Test
    public void getMaxLon(){
        assertEquals(bounds.getMaxLon(),5);
    }

    @Test
    public void getMinLat(){
        assertEquals(bounds.getMinLat(),1);
    }

    @Test
    public void getMinLon(){
        assertEquals(bounds.getMinLon(),2);
    }

   


}
