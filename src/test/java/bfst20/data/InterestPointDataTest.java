package bfst20.data;

import bfst20.logic.entities.InterestPoint;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class InterestPointDataTest {

    static InterestPointData interestPointData;
    static InterestPoint interestPoint;

    @BeforeAll
    static void setup() {
        interestPointData = InterestPointData.getInstance();
        interestPoint = new InterestPoint(9999, 8888);

    }

    @Test
    void getInstance() {
        assertEquals(interestPointData, InterestPointData.getInstance());
    }

    @Test
    void InterestPointLat() {
        assertEquals(interestPoint.getLatitude(), 9999);
    }

    @Test
    void InterestPointLong() {
        assertEquals(interestPoint.getLongitude(), 8888);
    }

    @Test
    void getAll_remove_addInterestPoint() {
        assertEquals(interestPointData.getAllInterestPoints().size(),0);
        interestPointData.saveInterestPoint(interestPoint);
        assertEquals(interestPointData.getAllInterestPoints().get(0),interestPoint);
        assertEquals(interestPointData.getAllInterestPoints().size(),1);
        interestPointData.removeInterestPoint(0);
        assertEquals(interestPointData.getAllInterestPoints().size(),0);
    }

}
