package ch.uzh.ifi.hase.soprafs24.Entities;

import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class NearestTest {

    private Nearest nearest;

    @BeforeEach
    public void setup() {
        nearest = new Nearest();
    }

    @Test
    public void testLatitudeLongitude() {
        double testLatitude = 45.123456;
        double testLongitude = -120.654321;

        nearest.setLatitude(testLatitude);
        nearest.setLongitude(testLongitude);

        assertEquals(testLatitude, nearest.getLatitude(), "Latitude should match the set value.");
        assertEquals(testLongitude, nearest.getLongitude(), "Longitude should match the set value.");
    }

    @Test
    public void testAllProperties() {
        nearest.setLatitude(37.7749);
        nearest.setLongitude(-122.4194);
        nearest.setElevation(15);
        nearest.setTimezone("PST");
        nearest.setCity("San Francisco");
        nearest.setName("Golden Gate");
        nearest.setProv("CA");
        nearest.setRegion("West Coast");
        nearest.setState("California");
        nearest.setInLatitude(37.8000);
        nearest.setInLongitude(-122.4183);
        nearest.setAltgeocode("94102");
        nearest.setDistance(5.75);

        assertEquals(37.7749, nearest.getLatitude(), "Latitude should match the set value.");
        assertEquals(-122.4194, nearest.getLongitude(), "Longitude should match the set value.");
        assertEquals(15, nearest.getElevation(), "Elevation should match the set value.");
        assertEquals("PST", nearest.getTimezone(), "Timezone should match the set value.");
        assertEquals("San Francisco", nearest.getCity(), "City should match the set value.");
        assertEquals("Golden Gate", nearest.getName(), "Name should match the set value.");
        assertEquals("CA", nearest.getProv(), "Province should match the set value.");
        assertEquals("West Coast", nearest.getRegion(), "Region should match the set value.");
        assertEquals("California", nearest.getState(), "State should match the set value.");
        assertEquals(37.8000, nearest.getInLatitude(), "Inner Latitude should match the set value.");
        assertEquals(-122.4183, nearest.getInLongitude(), "Inner Longitude should match the set value.");
        assertEquals("94102", nearest.getAltgeocode(), "Alt-Geocode should match the set value.");
        assertEquals(5.75, nearest.getDistance(), "Distance should match the set value.");
    }
}
