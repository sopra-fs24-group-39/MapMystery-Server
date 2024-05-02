package ch.uzh.ifi.hase.soprafs24.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Random;

public class GameCountryService {
    private List<String> countries;

    // Constructor to initialize the country list
    public GameCountryService() {
        countries = new ArrayList<>();
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            String countryName = locale.getDisplayCountry();
            if (!countryName.isEmpty()) {
                countries.add(countryName);
            }
        }
    }

    // Method to get a random country
    public String random_country() {
        Random rand = new Random();
        int index = rand.nextInt(countries.size()); // Get a random index
        return countries.get(index); // Return the country at the random index
    }

    // Optional: Method to get all countries - useful for debugging or other features
    public List<String> getAllCountries() {
        return new ArrayList<>(countries); // Return a copy of the countries list
    }
}
