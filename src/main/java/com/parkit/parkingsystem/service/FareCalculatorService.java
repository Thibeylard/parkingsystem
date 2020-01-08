package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.model.Ticket;

import java.text.DecimalFormat;

public class FareCalculatorService {

    /**
     * DecimalFormat use for fare format
     */
    private static DecimalFormat fareFormat = new DecimalFormat("#,##0.00");

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

        // Get duration in milliseconds by subtracting milliseconds equivalent outTime by milliseconds equivalent inTime.
        long durationInMs = ticket.getOutTime().toEpochMilli() - ticket.getInTime().toEpochMilli();
        // Divide by number of milliseconds in one hour to get hour equivalent parking duration.
        double durationInHour = durationInMs / (60. * 60. * 1000.);

        // Free 30 minutes parking feature implementation
        if (durationInHour < 0.5) {
            ticket.setPrice(0);
        } else {
            double finalPrice = 0;
            switch (ticket.getParkingSpot().getParkingType()) {
                case CAR:
                    finalPrice = durationInHour * Fare.CAR_RATE_PER_HOUR;
                    break;
                case BIKE:
                    finalPrice = durationInHour * Fare.BIKE_RATE_PER_HOUR;
                    break;
                default:
                    throw new IllegalArgumentException("Unknown Parking Type");
            }

            // Discount for recurring user feature implementation
            if (ticket.isDiscounted()) {
                finalPrice *= 0.95;
            }

            ticket.setPrice(formatFare(finalPrice));
        }
    }

    public static double formatFare(final double fare) {
        String priceToParse = fareFormat.format(fare).replace(',', '.');
        return Double.parseDouble((priceToParse));
    }
}
