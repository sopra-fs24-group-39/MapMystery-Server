//package ch.uzh.ifi.hase.soprafs24.service;
//
//import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
//import ch.uzh.ifi.hase.soprafs24.service.GameService;
//import ch.uzh.ifi.hase.soprafs24.service.StreetViewService;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.mockito.InjectMocks;
//import org.mockito.Mock;
//import org.mockito.MockitoAnnotations;
//import org.springframework.boot.web.client.RestTemplateBuilder;
//import org.springframework.http.HttpStatus;
//import org.springframework.http.ResponseEntity;
//import org.springframework.web.client.HttpClientErrorException;
//import org.springframework.web.client.RestTemplate;
//
//import java.util.List;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.*;
//
//public class GameServiceTest {
//
//    @Mock
//    private RestTemplate restTemplate;
//
//    @Mock
//    private StreetViewService streetViewService;
//
//    @InjectMocks
//    private GameService gameService;
//
//    @BeforeEach
//    public void setUp() {
//        MockitoAnnotations.openMocks(this);
//        gameService = new GameService(restTemplate);
//    }
//
//    @Test
//    public void testGetImageCoordinates_Success() throws InterruptedException {
//        Nearest nearest = mock(Nearest.class);
//        when(nearest.getLongitude()).thenReturn(8.5417);
//        when(nearest.getLatitude()).thenReturn(47.3769);
//        when(streetViewService.requestCoordinates()).thenReturn(nearest);
//
//        String url = "https://maps.googleapis.com/maps/api/streetview?size=400x400&radius=10000&location=47.3769,8.5417&key=AIzaSyCulQbj2J2o73qi_k7CIerQ-2NA5ExC7Lw&return_error_code=true";
//        ResponseEntity<String> responseEntity = new ResponseEntity<>(HttpStatus.OK);
//        when(restTemplate.getForEntity(url, String.class)).thenReturn(responseEntity);
//
//        List<Double> coordinates = gameService.get_image_coordinates();
//
//    }
//
//    @Test
//    public void testGetImageCoordinates_NotFound() throws InterruptedException {
//        Nearest nearest = mock(Nearest.class);
//        when(nearest.getLongitude()).thenReturn(8.5417);
//        when(nearest.getLatitude()).thenReturn(47.3769);
//        when(streetViewService.requestCoordinates()).thenReturn(nearest);
//
//        String url = "https://maps.googleapis.com/maps/api/streetview?size=400x400&radius=10000&location=47.3769,8.5417&key=AIzaSyCulQbj2J2o73qi_k7CIerQ-2NA5ExC7Lw&return_error_code=true";
//        when(restTemplate.getForEntity(url, String.class)).thenThrow(new HttpClientErrorException(HttpStatus.NOT_FOUND));
//
//        List<Double> coordinates = gameService.get_image_coordinates();
//
//        assertNull(coordinates);
//    }
//
//    @Test
//    public void testGetImageCoordinates_Exception() throws InterruptedException {
//        Nearest nearest = mock(Nearest.class);
//        when(nearest.getLongitude()).thenReturn(8.5417);
//        when(nearest.getLatitude()).thenReturn(47.3769);
//        when(streetViewService.requestCoordinates()).thenReturn(nearest);
//
//        String url = "https://maps.googleapis.com/maps/api/streetview?size=400x400&radius=10000&location=47.3769,8.5417&key=AIzaSyCulQbj2J2o73qi_k7CIerQ-2NA5ExC7Lw&return_error_code=true";
//        when(restTemplate.getForEntity(url, String.class)).thenThrow(new RuntimeException("An error occurred"));
//
//        List<Double> coordinates = gameService.get_image_coordinates();
//
//        assertNull(coordinates);
//    }
//}