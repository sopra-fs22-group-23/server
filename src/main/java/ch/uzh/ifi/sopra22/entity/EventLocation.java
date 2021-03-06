package ch.uzh.ifi.sopra22.entity;

import java.io.Serializable;


public class EventLocation implements Serializable {

    private String name;

    private float longitude;

    private float latitude;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getLongitude() {
        return longitude;
    }

    public void setLongitude(float longitude) {
        this.longitude = longitude;
    }

    public float getLatitude() {
        return latitude;
    }

    public void setLatitude(float latitude) {
        this.latitude = latitude;
    }
}
