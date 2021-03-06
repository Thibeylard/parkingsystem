package com.parkit.parkingsystem.model;

import java.time.Instant;

public class Ticket {
    /**
     * Unique identifier.
     */
    private int id;
    /**
     * Associated ParkingSpot instance.
     */
    private ParkingSpot parkingSpot;
    /**
     * Associated Vehicle Registration Number.
     */
    private String vehicleRegNumber;
    /**
     * Calculated price when resolved.
     */
    private double price;
    /**
     * Creation time : Instant instance truncated to ChronoUnit.MINUTES
     */
    private Instant inTime;
    /**
     * Exit time : Instant instance truncated to ChronoUnit.MINUTES
     */
    private Instant outTime = null;
    /**
     * Whether ticket has a discount applied.
     */
    private boolean isDiscounted = false;

    /**
     * @return unique identifier
     */
    public int getId() {
        return id;
    }

    /**
     * @param idToSet new unique identifier
     */
    public void setId(final int idToSet) {
        this.id = idToSet;
    }

    /**
     * @return associated ParkingSpot
     */
    public ParkingSpot getParkingSpot() {
        return parkingSpot;
    }

    /**
     * @param parkingSpotToSet new ParkingSpot instance
     */
    public void setParkingSpot(final ParkingSpot parkingSpotToSet) {
        this.parkingSpot = parkingSpotToSet;
    }

    /**
     * @return associated vehicle registration number.
     */
    public String getVehicleRegNumber() {
        return vehicleRegNumber;
    }

    /**
     * @param vehicleRegNumberToSet new vehicle registration number.
     */
    public void setVehicleRegNumber(final String vehicleRegNumberToSet) {
        this.vehicleRegNumber = vehicleRegNumberToSet;
    }

    /**
     * @return defined price.
     */
    public double getPrice() {
        return price;
    }

    /**
     * @param priceToSet new price.
     */
    public void setPrice(final double priceToSet) {
        this.price = priceToSet;
    }

    /**
     * @return ticket creation time.
     */
    public Instant getInTime() {
        return inTime;
    }

    /**
     * @param inTimeToSet new creation time.
     */
    public void setInTime(final Instant inTimeToSet) {
        this.inTime = inTimeToSet;
    }

    /**
     * @return resolution time.
     */
    public Instant getOutTime() {
        return outTime;
    }

    /**
     * @param outTimeToSet new resolution time.
     */
    public void setOutTime(final Instant outTimeToSet) {
        this.outTime = outTimeToSet;
    }

    /**
     * @return true if discount on ticket.
     */
    public boolean isDiscounted() {
        return this.isDiscounted;
    }

    /**
     * @param stateToSet new isDiscounted boolean state.
     */
    public void setDiscounted(boolean stateToSet) {
        this.isDiscounted = stateToSet;
    }
}
