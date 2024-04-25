package ch.uzh.ifi.hase.soprafs24.service;

import java.util.List;

public class LoggingGameServiceDecorator extends GameService {
    private final GameService gameService;

    public LoggingGameServiceDecorator(GameService gameService) {
        super(gameService.getRestTemplate());
        this.gameService = gameService;
    }

    @Override
    public List<Double> get_image_coordinates() throws InterruptedException {
        List<Double> coordinates = gameService.get_image_coordinates();
        if (coordinates != null) {
            // Log the coordinates
            System.out.println("Coordinates returned: " + coordinates);
        } else {
            System.out.println("No coordinates returned.");
        }
        return coordinates;
    }
}
