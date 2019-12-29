package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


import java.time.Instant;
import java.util.Date;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class FareCalculatorServiceTest {

    /**
     * Class Under Test.
     */
    private static FareCalculatorService fareCalculatorService;
    /**
     * Real Ticket class instance.
     */
    private Ticket ticket;

    /**
     * Initialize class under test.
     */
    @BeforeAll
    private static void setUp() {
        fareCalculatorService = new FareCalculatorService();
    }

    /**
     * Create Ticket instance.
     */
    @BeforeEach
    private void setUpPerTest() {
        ticket = new Ticket();
    }

    /**
     * Check consistency between car hour rate and one hour parking fare.
     */
    @Test
    public void Given_oneHourCarParking_When_calculateFare_Then_priceEqualCarRatePerHour() {
        Instant inTime = Instant.now().minusMillis((60 * 60 * 1000));
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), Fare.CAR_RATE_PER_HOUR);
    }

    /**
     * Check consistency between bike hour rate and one hour parking fare.
     */
    @Test
    public void Given_oneHourBikeParking_When_calculateFare_Then_priceEqualBikeRatePerHour() {
        Instant inTime = Instant.now().minusMillis((60 * 60 * 1000));
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(Fare.BIKE_RATE_PER_HOUR,ticket.getPrice());
    }

    /**
     * Check exception handling in unknown vehicle fail case.
     */
    @Test
    public void Given_oneHourParkingForUnknownVehicle_When_calculateFare_Then_throwsNullPointerException() {
        Instant inTime = Instant.now().minusMillis((60 * 60 * 1000));
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, null, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(NullPointerException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Check consistency between car hour rate and one hour parking fare.
     */
    @Test
    public void Given_inTimeGreaterThanOutTimeParking_When_calculateFare_Then_throwsIllegalArgumentException() {
        Instant inTime = Instant.now().plusMillis((60 * 60 * 1000));
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        assertThrows(IllegalArgumentException.class, () -> fareCalculatorService.calculateFare(ticket));
    }

    /**
     * Check consistency between bike hour rate and three quarter of hour parking fare.
     */
    @Test
    public void Given_threeQuarterOfHourBikeParking_When_calculateFare_Then_priceEqualTo75PercentOfBikeHourRate() {
        Instant inTime = Instant.now().minusMillis((45 * 60 * 1000)); //45 minutes parking time should give 3/4th parking fare
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.BIKE_RATE_PER_HOUR), ticket.getPrice());
    }

    /**
     * Check consistency between car hour rate and three quarter of hour parking fare.
     */
    @Test
    public void Given_threeQuarterOfHourCarParking_When_calculateFare_Then_priceEqualTo75PercentOfCarHourRate() {
        Instant inTime = Instant.now().minusMillis((45 * 60 * 1000)); //45 minutes parking time should give 3/4th parking fare
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((0.75 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

    /**
     * Check consistency between car hour rate and one day parking fare.
     */
    @Test
    public void Given_oneDayParking_When_calculateFare_Then_priceEqualTo24TimesHourRate() {
        Instant inTime = Instant.now().minusMillis((24 * 60 * 60 * 1000)); //24 hours parking time should give 24 * parking fare per hour
        Instant outTime = Instant.now();
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * Fare.CAR_RATE_PER_HOUR), ticket.getPrice());
    }

}
