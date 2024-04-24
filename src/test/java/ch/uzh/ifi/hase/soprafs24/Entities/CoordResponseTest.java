package ch.uzh.ifi.hase.soprafs24.Entities;

import ch.uzh.ifi.hase.soprafs24.entity.CoordResponse;
import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
import ch.uzh.ifi.hase.soprafs24.entity.Osmtags;
import ch.uzh.ifi.hase.soprafs24.entity.Adminareas;
import ch.uzh.ifi.hase.soprafs24.entity.Major;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class CoordResponseTest {

    private CoordResponse response;

    @BeforeEach
    public void setup() {
        response = new CoordResponse();
    }

    @Test
    public void testConstructor_initialState() {
        assertNull(response.getNearest());
        assertNull(response.getOsmtags());
        assertNull(response.getAdminareas());
        assertNull(response.getMajor());
        assertNull(response.getGeocode());
        assertNull(response.getGeonumber());
        assertNull(response.getThreegeonames());
    }

    @Test
    public void testSettersAndGetters() {
        Nearest nearest = new Nearest();
        Osmtags osmtags = new Osmtags();
        Adminareas adminareas = new Adminareas();
        Major major = new Major();

        response.setNearest(nearest);
        response.setOsmtags(osmtags);
        response.setAdminareas(adminareas);
        response.setMajor(major);
        response.setGeocode("12345");
        response.setGeonumber("67890");
        response.setThreegeonames("ExampleName");

        assertSame(nearest, response.getNearest());
        assertSame(osmtags, response.getOsmtags());
        assertSame(adminareas, response.getAdminareas());
        assertSame(major, response.getMajor());
        assertEquals("12345", response.getGeocode());
        assertEquals("67890", response.getGeonumber());
        assertEquals("ExampleName", response.getThreegeonames());
    }
}
