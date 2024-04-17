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

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }
}
