package bfst20.data;

import bfst20.logic.entities.InterestPoint;

import java.util.LinkedList;
import java.util.List;

public class InterestPointData {
    private static InterestPointData interestPointData;
    private List<InterestPoint> interestPoints;


    private InterestPointData() {
        interestPoints = new LinkedList<>();
    }

    public static InterestPointData getInstance() {
        if (interestPointData == null) {
            interestPointData = new InterestPointData();
        }

        return interestPointData;
    }

    public void clearData(){
        interestPoints = new LinkedList<>();
    }

    public void saveInterestPoint(InterestPoint interestPoint) {
        interestPoints.add(interestPoint);
    }

    public void removeInterestPoint(int key) {
        interestPoints.remove(key);
    }

    public List<InterestPoint> getAllInterestPoints() {
        return interestPoints;
    }


}
