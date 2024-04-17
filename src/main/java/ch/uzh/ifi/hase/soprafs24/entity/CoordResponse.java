package ch.uzh.ifi.hase.soprafs24.entity;
import javax.xml.bind.annotation.*;

@XmlRootElement(name = "geodata")
@XmlAccessorType(XmlAccessType.FIELD)
public class CoordResponse {
    @XmlElement(name = "nearest")
    private Nearest nearest;

    @XmlElement(name = "osmtags")
    private Osmtags osmtags;

    @XmlElement(name = "adminareas")
    private Adminareas adminareas;

    @XmlElement(name = "major")
    private Major major;

    @XmlElement
    private String geocode;

    @XmlElement
    private String geonumber;

    @XmlElement
    private String threegeonames;

    // Getters and setters
    public Nearest getNearest() {
        return nearest;
    }

    public void setNearest(Nearest nearest) {
        this.nearest = nearest;
    }

    public Osmtags getOsmtags() {
        return osmtags;
    }

    public void setOsmtags(Osmtags osmtags) {
        this.osmtags = osmtags;
    }

    public Adminareas getAdminareas() {
        return adminareas;
    }

    public void setAdminareas(Adminareas adminareas) {
        this.adminareas = adminareas;
    }

    public Major getMajor() {
        return major;
    }

    public void setMajor(Major major) {
        this.major = major;
    }

    public String getGeocode() {
        return geocode;
    }

    public void setGeocode(String geocode) {
        this.geocode = geocode;
    }

    public String getGeonumber() {
        return geonumber;
    }

    public void setGeonumber(String geonumber) {
        this.geonumber = geonumber;
    }

    public String getThreegeonames() {
        return threegeonames;
    }

    public void setThreegeonames(String threegeonames) {
        this.threegeonames = threegeonames;
    }
}

@XmlAccessorType(XmlAccessType.FIELD)
class Osmtags {
    // Define fields according to XML structure
    // You can add annotations if necessary
}

@XmlAccessorType(XmlAccessType.FIELD)
class Adminareas {
    // Define fields according to XML structure
    // You can add annotations if necessary
}

@XmlAccessorType(XmlAccessType.FIELD)
class Major {
    // Define fields according to XML structure
    // You can add annotations if necessary
}
