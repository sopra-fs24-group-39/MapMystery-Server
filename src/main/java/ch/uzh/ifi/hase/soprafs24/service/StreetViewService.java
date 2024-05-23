package ch.uzh.ifi.hase.soprafs24.service;

import ch.uzh.ifi.hase.soprafs24.entity.CoordResponse;
import ch.uzh.ifi.hase.soprafs24.entity.Nearest;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import java.io.StringReader;

public class StreetViewService {
    private final static String API_KEY = "AIzaSyCSCKEJZ9BWDpA2lul7Crnjw_J2afXfr9s";

    private final RestTemplate restTemplate;

    public StreetViewService(RestTemplateBuilder restTemplateBuilder){
        this.restTemplate = restTemplateBuilder.build();
    }

    public Nearest requestCoordinates() {
        String url = "https://api.3geonames.org/?randomland=yes";
        ResponseEntity<String> responseEntity = restTemplate.getForEntity(url, String.class);
        String xmlResponse = responseEntity.getBody(); // XML RESPONSE BODY

        CoordResponse coordResponse = parseXmlResponse(xmlResponse);
        Nearest nearest = coordResponse.getNearest();

        return nearest; // RETURNS ENTITY coordResponse
    }

    private CoordResponse parseXmlResponse(String xmlResponse){
        // Use JAXB to unmarshal the XML response into a CoordResponse object
        try {
            JAXBContext jaxbContext = JAXBContext.newInstance(CoordResponse.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            StringReader reader = new StringReader(xmlResponse);
            return (CoordResponse) unmarshaller.unmarshal(reader);
        } catch (JAXBException e) {
            e.printStackTrace();
            return null;
        }

    }


}
