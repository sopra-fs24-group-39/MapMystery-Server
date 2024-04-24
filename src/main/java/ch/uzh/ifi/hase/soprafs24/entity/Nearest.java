package ch.uzh.ifi.hase.soprafs24.entity;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;

@XmlAccessorType(XmlAccessType.FIELD)
public class Nearest {
    @XmlElement(name = "latt")
    private double latitude;

    @XmlElement(name = "longt")
    private double longitude;

    @XmlElement
    private int elevation;

    @XmlElement
    private String timezone;

    @XmlElement
    private String city;

    @XmlElement
    private String name;

    @XmlElement
    private String prov;

    @XmlElement
    private String region;

    @XmlElement
    private String state;

    @XmlElement(name = "inlatt")
    private double inLatitude;

    @XmlElement(name = "inlongt")
    private double inLongitude;

    @XmlElement
    private String altgeocode;

    @XmlElement
    private double distance;

    // Getters
    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public int getElevation() {
        return elevation;
    }

    public String getTimezone() {
        return timezone;
    }

    public String getCity() {
        return city;
    }

    public String getName() {
        return name;
    }

    public String getProv() {
        return prov;
    }

    public String getRegion() {
        return region;
    }

    public String getState() {
        return state;
    }

    public double getInLatitude() {
        return inLatitude;
    }

    public double getInLongitude() {
        return inLongitude;
    }

    public String getAltgeocode() {
        return altgeocode;
    }

    public double getDistance() {
        return distance;
    }

    // Setters
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public void setElevation(int elevation) {
        this.elevation = elevation;
    }

    public void setTimezone(String timezone) {
        this.timezone = timezone;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProv(String prov) {
        this.prov = prov;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setState(String state) {
        this.state = state;
    }

    public void setInLatitude(double inLatitude) {
        this.inLatitude = inLatitude;
    }

    public void setInLongitude(double inLongitude) {
        this.inLongitude = inLongitude;
    }

    public void setAltgeocode(String altgeocode) {
        this.altgeocode = altgeocode;
    }

    public void setDistance(double distance) {
        this.distance = distance;
    }
}
