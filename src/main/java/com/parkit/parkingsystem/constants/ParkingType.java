package com.parkit.parkingsystem.constants;

public enum ParkingType {
    /**
     * Cars, vans.
     */
    CAR(1.5),
    /**
    Bikes, scooters.
     */
    BIKE(1.0);

    private final double fare;

    ParkingType(double fareToSet) {
        fare = fareToSet;
    }

    public double getFare() {
        return fare;
    }
}
