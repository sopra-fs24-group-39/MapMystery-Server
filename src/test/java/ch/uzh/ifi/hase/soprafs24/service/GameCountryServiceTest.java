package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class GameCountryServiceTest {
    private GameCountryService service;

    @BeforeEach
    void setUp() {
        service = new GameCountryService();
    }

    @Test
    void testCountryListIsNotEmpty() {
        assertFalse(service.getAllCountries().isEmpty(), "Country list should not be empty");
    }

    @Test
    void testRandomCountryIsValid() {
        String randomCountry = service.random_country();
        assertNotNull(randomCountry, "Random country should not be null");
        assertTrue(service.getAllCountries().contains(randomCountry), "Random country should be in the country list");
    }

    @Test
    void testAllCountries() {
        List<String> countries = service.getAllCountries();
        assertTrue(countries.contains("United States"), "List should contain 'United States'");
        assertTrue(countries.contains("Switzerland"), "List should contain 'Switzerland'");
        assertTrue(countries.contains("India"), "List should contain 'India'");
        // Add more assertions as needed for expected countries
    }
}
