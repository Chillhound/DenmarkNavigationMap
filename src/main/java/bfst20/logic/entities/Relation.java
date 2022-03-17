package bfst20.logic.entities;

import bfst20.logic.misc.OSMType;
import bfst20.logic.misc.OSMElement;

import java.util.ArrayList;

public class Relation implements OSMElement {
    private ArrayList<Long> members;
    private boolean multipolygon;
    private OSMType OSMType;
    private String name;
    private long id;

    public Relation(long id) {
        this.id = id;
        members = new ArrayList<>();
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

    public String getName() {
        return name;
    }


    public void addMember(long member, String type) {
        if (!type.equals("way")) return;
        members.add(member);
    }

    public ArrayList<Long> getMembers() {
        return members;
    }

    public long getId() {
        return id;
    }

}
