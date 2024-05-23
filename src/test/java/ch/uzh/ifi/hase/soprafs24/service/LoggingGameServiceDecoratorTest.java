package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.service.GameService;
import ch.uzh.ifi.hase.soprafs24.service.LoggingGameServiceDecorator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;

public class LoggingGameServiceDecoratorTest {

    @Mock
    private GameService gameService;

    @InjectMocks
    private LoggingGameServiceDecorator loggingGameServiceDecorator;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        loggingGameServiceDecorator = new LoggingGameServiceDecorator(gameService);
    }

    @Test
    public void testGetImageCoordinates_WithCoordinates() throws InterruptedException {
        List<Double> mockCoordinates = Arrays.asList(47.3769, 8.5417);
        when(gameService.get_image_coordinates()).thenReturn(mockCoordinates);

        List<Double> coordinates = loggingGameServiceDecorator.get_image_coordinates();

        assertEquals(mockCoordinates, coordinates);
        verify(gameService, times(1)).get_image_coordinates();
        // You can also use a logger to capture the output and assert the log messages if needed
    }

    @Test
    public void testGetImageCoordinates_NoCoordinates() throws InterruptedException {
        when(gameService.get_image_coordinates()).thenReturn(null);

        List<Double> coordinates = loggingGameServiceDecorator.get_image_coordinates();

        assertEquals(null, coordinates);
        verify(gameService, times(1)).get_image_coordinates();
        // You can also use a logger to capture the output and assert the log messages if needed
    }
}