package bfst20.logic.entities;

import java.io.Serializable;

public class Address implements Serializable {
    private String city,
            housenumber,
            postcode,
            street;
    private float lat, lon;
    private long nodeID;

    public Address(String city,
                   String housenumber,
                   String postcode,
                   String street,
                   float lat,
                   float lon, long nodeID) {
        this.city = city;
        this.street = street;
        this.housenumber = housenumber;
        this.postcode = postcode;
        this.lat = lat;
        this.lon = lon;
        this.nodeID = nodeID;
    }



    public String getStreet(){
        return street;
    }

    public String getHousenumber() {
        return housenumber;
    }

    public String getPostcode() {
        return postcode;
    }

    public String getCity() {
        return city;
    }

    public float getLat() {
        return lat;
    }

    public float getLon() {
        return lon;
    }

    @Override
    public String toString() {
        return street + " " + housenumber + " " + postcode;
    }
}
