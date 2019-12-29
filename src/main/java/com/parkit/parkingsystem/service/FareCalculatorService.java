package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

public class FareCalculatorService {

    /**
     * Calculate parking fare based on user ticket.
     * @param ticket passed from ParkingService.processExitingVehicle()
     */
    public void calculateFare(final Ticket ticket) {
        if ((ticket.getOutTime() == null) || (ticket.getOutTime().isBefore(ticket.getInTime()))) {
            assert ticket.getOutTime() != null;
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        //TODO: Some tests are failing here. Need to check if this logic is correct
        long duration = ticket.getOutTime().toEpochMilli() - ticket.getInTime().toEpochMilli();

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                ticket.setPrice(duration * Fare.CAR_RATE_PER_HOUR);
                break;
            case BIKE:
                ticket.setPrice(duration * Fare.BIKE_RATE_PER_HOUR);
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }
    }
}
