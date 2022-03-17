package bfst20.logic;

import bfst20.logic.entities.Address;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.Queue;

public class TSTTest {


    @Test
    public void put(){

        Address address = new Address("Hilleroed", "10", "3400", "test", 1, 1, 1);

        bfst20.logic.ternary.TST tst = new bfst20.logic.ternary.TST();
        tst.put("Hello", address);
        tst.put("Hej", address);
        tst.put("Hellsevej", address);

        assert tst.getSize() == 11;
    }

    @Test
    public void get(){
        Address addressRes = new Address("Not hillerod", "10", "3400", "test", 1, 1, 1);
        Address address = new Address("Hilleroed", "10", "3400", "test", 1, 1, 1);

        bfst20.logic.ternary.TST tst = new bfst20.logic.ternary.TST();
        tst.put("Hello", address);
        tst.put("Hej", addressRes);
        tst.put("Hellsevej", address);
        tst.put("Hellsevej", address);

        assert tst.get("Hej").get(0) == addressRes;
    }

    @Test
    public void keysWithPrefix(){
        Address Address1 = new Address("Hilleroed", "10", "3400", "Hello", 1, 1, 1);
        Address Address2 = new Address("Hilleroed", "10", "3400", "Hej", 1, 1, 1);
        Address Address3 = new Address("Hilleroed", "10", "3400", "Hellsevej", 1, 1, 1);

        bfst20.logic.ternary.TST tst = new bfst20.logic.ternary.TST();
        tst.put("Hello", Address1);
        tst.put("Hej", Address2);
        tst.put("Hellsevej", Address3);

        Queue<Address> addresses = tst.keysWithPrefix("Hello");

        assert addresses.size() == 1;
    }

    @Test
    public void spaceTest(){
        Address Address1 = new Address("Hjørring", "10", "9800", "Skagen landevej", 1, 1, 1);
        bfst20.logic.ternary.TST tst = new bfst20.logic.ternary.TST();

        tst.put(Address1.getStreet(), Address1);

        Queue<Address> addresses = tst.keysWithPrefix("Skagen landevej");

        assert addresses.poll() == Address1;
    }
}
