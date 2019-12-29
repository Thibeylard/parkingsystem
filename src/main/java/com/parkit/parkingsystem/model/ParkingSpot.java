package com.parkit.parkingsystem.model;

import com.parkit.parkingsystem.constants.ParkingType;

public class ParkingSpot {
    /**
     * Number as identifier.
     */
    private int number;
    /**
     * parkingType either CAR or BIKE.
     */
    private ParkingType parkingType;
    /**
     * isAvailable if no running ticket on it.
     */
    private boolean isAvailable;

    /**
     * Constructor.
     * @param numberToSet as identifier
     * @param parkingTypeToSet as CAR or BIKE
     * @param available if no ticket running on it.
     */
    public ParkingSpot(final int numberToSet, final ParkingType parkingTypeToSet, final boolean available) {
        this.number = numberToSet;
        this.parkingType = parkingTypeToSet;
        this.isAvailable = available;
    }

    /**
     * @return number as identifier
     */
    public int getId() {
        return number;
    }

    /**
     * @param numberToSet as identifier
     */
    public void setId(final int numberToSet) {
        this.number = numberToSet;
    }

    /**
     * @return parkingType as ParkingType.CAR or ParkingType.BIKE
     */
    public ParkingType getParkingType() {
        return parkingType;
    }

    /**
     * @param parkingTypeToSet : ParkingType.CAR or ParkingType.BIKE
     */
    public void setParkingType(final ParkingType parkingTypeToSet) {
        this.parkingType = parkingTypeToSet;
    }

    /**
     * @return true if no running ticket on it.
     */
    public boolean isAvailable() {
        return isAvailable;
    }

    /**
     * @param available : true or false
     */
    public void setAvailable(final boolean available) {
        isAvailable = available;
    }

    /**
     * Overriding Objet.equals(). Compare number member attribute.
     * @param o as any Object instance
     * @return true if equal number member attribute.
     */
    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        ParkingSpot that = (ParkingSpot) o;
        return number == that.number;
    }

    /**
     * Overriding Objet.hashCode().
     * @return number member attribute.
     */
    @Override
    public int hashCode() {
        return number;
    }
}
