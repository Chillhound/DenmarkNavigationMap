package bfst20.logic.entities;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class AddressTest{
    public static Address address;
    
    @BeforeAll
    public static void setup(){
        address = new Address("Køge","1","4600","Farstrupvej",56,54,1);
    }

    @Test
    public void getstreet(){
        assertEquals(address.getStreet(),"Farstrupvej");
    }

    @Test
    public void getHousenumber(){
        assertEquals(address.getHousenumber(),"1");
    }

    @Test
    public void getPostcode(){
        assertEquals(address.getPostcode(),"4600");
    }

    @Test
    public void getCity(){
        assertEquals(address.getCity(),"Køge");
    }

    @Test
    public void getLat(){
        assertEquals(address.getLat(),56);
    }

    @Test
    public void getLon(){
        assertEquals(address.getLon(),54);
    }

    @Test
    public void toStringTest(){
        assertEquals(address.toString(),"Farstrupvej 1 4600");
    }


}
