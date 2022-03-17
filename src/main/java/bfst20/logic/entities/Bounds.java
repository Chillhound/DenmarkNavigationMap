package bfst20.logic.entities;

import java.io.Serializable;

public class Bounds implements Serializable {

    float maxLat, minLat, maxLon, minLon;

    public Bounds(float maxLat, float minLat, float maxLon, float minLon) {
        this.maxLat = maxLat;
        this.minLat = minLat;
        this.maxLon = maxLon;
        this.minLon = minLon;
    }

    public float getMaxLat() {
        return maxLat;
    }

    public float getMaxLon() {
        return maxLon;
    }

    public float getMinLon() {
        return minLon;
    }

    public float getMinLat() {
        return minLat;
    }

}
