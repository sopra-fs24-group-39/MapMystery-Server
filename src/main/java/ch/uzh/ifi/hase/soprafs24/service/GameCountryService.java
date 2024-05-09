package ch.uzh.ifi.hase.soprafs24.service;

import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class GameCountryService {
    private Map<String, String> countryMap; // Maps country code to country name

    // Constructor to initialize the country list and map
    public GameCountryService() {
        countryMap = new HashMap<>();
        String[] countryCodes = Locale.getISOCountries();
        for (String countryCode : countryCodes) {
            Locale locale = new Locale("", countryCode);
            String countryName = locale.getDisplayCountry();
            if (!countryName.isEmpty()) {
                countryMap.put(countryCode, countryName);
            }
        }
    }

    // Method to get a random country along with its code
    public Map<String, String> randomCountry() {
        Random rand = new Random();
        List<String> keys = new ArrayList<>(countryMap.keySet());
        String randomKey = keys.get(rand.nextInt(keys.size()));
        Map<String, String> result = new HashMap<>();
        result.put("code", randomKey);
        result.put("name", countryMap.get(randomKey));
        return result; // Return the country code and name
    }

    // Optional: Method to get all countries - useful for debugging or other features
    public Map<String, String> getAllCountries() {
        return new HashMap<>(countryMap); // Return a copy of the country map
    }
}