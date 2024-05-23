package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

public class GameService {

    private final static String API_KEY = "AIzaSyCulQbj2J2o73qi_k7CIerQ-2NA5ExC7Lw";
    private final RestTemplate restTemplate;

    public GameService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<Double> get_image_coordinates() throws InterruptedException {
        StreetViewService streetViewService = new StreetViewService(new RestTemplateBuilder());
        int calls = 0;

        while (calls < 20) {
            Nearest nearest = streetViewService.requestCoordinates();
            double longitude = nearest.getLongitude();
            double latitude =  nearest.getLatitude();

            String url = "https://maps.googleapis.com/maps/api/streetview?size=400x400&radius=10000&location=" + latitude
                    + "," + longitude + "&key=" + API_KEY + "&return_error_code=true";
            String clickableUrl = "\u001B" + url + "\u001B\\";
            System.out.println("Trying url " + clickableUrl);
            //TimeUnit.SECONDS.sleep(0.01);

            try {
                ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

                if (response.getStatusCode().is2xxSuccessful()) {
                    List<Double> doubleList = new ArrayList<>();
                    doubleList.add(longitude);
                    doubleList.add(latitude);
                    return doubleList;
                } else if (response.getStatusCode() == HttpStatus.NOT_FOUND) {
                    calls++;
                }
            } catch (HttpClientErrorException.NotFound e) {
                calls++;
            } catch (Exception e) {
                System.out.println("An error occurred: " + e.getMessage());
                break;
            }
        }
        System.out.println("Couldn't find valid image within call limit");
        return null;
    }
    public RestTemplate getRestTemplate(){ return  this.restTemplate;}
}



