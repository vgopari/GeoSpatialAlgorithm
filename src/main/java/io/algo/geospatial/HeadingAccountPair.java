package io.algo.geospatial;

public class HeadingAccountPair {
    private double heading;
    private LatLngDto latLngDto;

    public HeadingAccountPair(double heading, LatLngDto latLngDto) {
        this.heading = heading;
        this.latLngDto = latLngDto;
    }

    public double getHeading() {
        return heading;
    }

    public LatLngDto getLatLngDto() {
        return latLngDto;
    }

    public void setHeading(double heading) {
        this.heading = heading;
    }

    public void setLatLngDto(LatLngDto latLngDto) {
        this.latLngDto = latLngDto;
    }
}