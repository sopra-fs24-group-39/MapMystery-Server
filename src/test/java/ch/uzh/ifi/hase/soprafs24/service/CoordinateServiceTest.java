package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.boot.web.client.RestTemplateBuilder;

import java.util.List;

public class CoordinateServiceTest {


    @Disabled("Test calls API skipped until implementation complete.")
    @Test
    void testGetCoordinates(){
        StreetViewService streetViewService = new StreetViewService(new RestTemplateBuilder());
        Nearest nearest = streetViewService.requestCoordinates();

        assert(nearest.getLatitude() < 90);
        assert(nearest.getLatitude() > -90);
        assert(nearest.getLongitude() < 90);
        assert(nearest.getLongitude() > -90);

    }

    //@Disabled("Calls Google API")
    @Test
    void testRealImage() throws InterruptedException {
        GameService gameService = new GameService(new RestTemplateBuilder().build());
        List<Double> l = gameService.get_image_coordinates();


    }

}
