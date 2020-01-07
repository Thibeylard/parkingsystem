package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.text.DecimalFormat;

public class FareCalculatorService {

    /**
     * Calculate parking fare based on user ticket.
     *
     * @param ticket passed from ParkingService.processExitingVehicle()
     */
    public void calculateFare(final Ticket ticket) {
        if ((ticket.getOutTime() == null)) {
            throw new IllegalArgumentException("No out time provided");
        }
        if ((ticket.getOutTime().isBefore(ticket.getInTime()))) {
            throw new IllegalArgumentException("Out time provided is incorrect:" + ticket.getOutTime().toString());
        }

        long durationInMs = ticket.getOutTime().toEpochMilli() - ticket.getInTime().toEpochMilli();
        double durationInHour = durationInMs / 3600000.;
        String priceToParse = "";

        DecimalFormat df = new DecimalFormat();
        df.setMaximumFractionDigits(2);

        switch (ticket.getParkingSpot().getParkingType()) {
            case CAR:
                priceToParse = df.format(durationInHour * Fare.CAR_RATE_PER_HOUR).replace(',', '.');
                break;
            case BIKE:
                priceToParse = df.format(durationInHour * Fare.BIKE_RATE_PER_HOUR).replace(',', '.');
                break;
            default:
                throw new IllegalArgumentException("Unknown Parking Type");
        }

        ticket.setPrice(Double.parseDouble((priceToParse)));
    }
}
