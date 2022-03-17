package bfst20.logic.entities;

import bfst20.logic.misc.OSMType;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class RelationTest {
    public static Relation relation;




    @BeforeAll
    public static void setup(){
        relation = new Relation(10);
    }

    //Combined two methods, not to create excessive test.
    @Test
    public void get_setName() {
        relation.setName("Building");
        assertEquals(relation.getName(), "Building");
    }


    @Test
    public void is_setMultipolygon() {
        assertEquals(relation.isMultipolygon(), false);
        relation.setMultipolygon(true);
        assertEquals(relation.isMultipolygon(), true);
    }

    @Test
    public void is_setOSMType(){
        relation.setOSMType(OSMType.BUILDING);
        assertEquals(relation.getOSMType(),OSMType.BUILDING);
    }

    @Test
    public void get_addMember() {
        ArrayList<Long> member = new ArrayList<>();
        member.add((long)500);
        relation.addMember(500, "way");
        assertEquals(relation.getMembers(),member);
    }

    @Test
    public void getId(){
        assertEquals(relation.getId(),10);
    }
}
