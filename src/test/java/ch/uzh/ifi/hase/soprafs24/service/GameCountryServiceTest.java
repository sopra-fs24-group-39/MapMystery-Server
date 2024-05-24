package ch.uzh.ifi.hase.soprafs24.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;
import java.util.Map;

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
        // Fetch a random country which is now a Map containing both code and name
        Map<String, String> randomCountry = service.randomCountry();

        // Assert that the returned country map is not null
        assertNotNull(randomCountry, "Random country should not be null");

        // Assert that the country map contains keys "code" and "name"
        assertTrue(randomCountry.containsKey("code"), "Country map should contain a code");
        assertTrue(randomCountry.containsKey("name"), "Country map should contain a name");

        // Fetch all countries
        Map<String, String> allCountries = service.getAllCountries();

        // Assert that the code and name from randomCountry are valid and present in allCountries
        String countryCode = randomCountry.get("code");
        String countryName = randomCountry.get("name");
        assertNotNull(countryCode, "Country code should not be null");
        assertNotNull(countryName, "Country name should not be null");
        assertTrue(allCountries.containsKey(countryCode), "Random country code should be in the country list");
        assertEquals(allCountries.get(countryCode), countryName, "Random country name should match the name associated with its code in the full country list");
    }

    @Test
    void testAllCountries() {
        Map<String, String> countries = service.getAllCountries();

        // Check if the country names are present in the map values
        assertTrue(countries.containsValue("United States"), "List should contain 'United States'");
        assertTrue(countries.containsValue("Switzerland"), "List should contain 'Switzerland'");
        assertTrue(countries.containsValue("India"), "List should contain 'India'");
    }
}