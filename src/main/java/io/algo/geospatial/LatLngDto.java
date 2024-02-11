package io.algo.geospatial;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

public class LatLngDto implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private double lat;

    private double lng;

    private Long userId;

    private Double heading;
    /**
     * Constructs a location with a latitude/longitude pair.
     *
     * @param lat The latitude of this location.
     * @param lng The longitude of this location.
     */
    public LatLngDto(double lat, double lng) {
        this.setLat(lat);
        this.setLng(lng);
    }

    public Double getHeading() {
        return heading;
    }

    public void setHeading(Double heading) {
        this.heading = heading;
    }

    /** Serialisation constructor. */
    public LatLngDto() {}

    /** The latitude of this location. */
    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    /** The longitude of this location. */
    public double getLng() {
        return lng;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Long getUserId() {
        return userId;
    }

    public void setUserId(Long userId) {
        this.userId = userId;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        LatLngDto latLng = (LatLngDto) o;
        return Double.compare(latLng.getLat(), getLat()) == 0 && Double.compare(latLng.getLng(), getLng()) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(getLat(), getLng(), getUserId());
    }
}
