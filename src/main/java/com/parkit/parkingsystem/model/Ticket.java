package com.parkit.parkingsystem.model;

import java.util.Date;

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
     * Creation time.
     */
    private Date inTime;
    /**
     * Resolution time.
     */
    private Date outTime;

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
    public Date getInTime() {
        return inTime;
    }

    /**
     * @param inTimeToSet new creation time.
     */
    public void setInTime(final Date inTimeToSet) {
        this.inTime = inTimeToSet;
    }

    /**
     * @return resolution time.
     */
    public Date getOutTime() {
        return outTime;
    }

    /**
     * @param outTimeToSet new resolution time.
     */
    public void setOutTime(final Date outTimeToSet) {
        this.outTime = outTimeToSet;
    }
}
