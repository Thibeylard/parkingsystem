package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import org.junit.jupiter.api.*;


import java.time.Instant;

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
        this.ticket = new Ticket();
    }

    /**
     * Check consistency between car hour rate and one hour parking fare.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : one car hour")
    public void Given_oneHourCarParking_When_calculateFare_Then_priceEqualCarRatePerHour() {
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(60 * 60 * 1000);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(ticket.getPrice(), ParkingType.CAR.getFare());
    }

    /**
     * Check consistency between bike hour rate and one hour parking fare.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : one bike hour")
    public void Given_oneHourBikeParking_When_calculateFare_Then_priceEqualBikeRatePerHour() {
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(60 * 60 * 1000);
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);

        assertEquals(ParkingType.BIKE.getFare(), ticket.getPrice());
    }

    /**
     * Check exception handling in unknown vehicle fail case.
     */
    @Test
    @DisplayName("No such ParkingType Exception")
    public void Given_oneHourParkingForUnknownVehicle_When_calculateFare_Then_throwsNullPointerException() {
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(60 * 60 * 1000);
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
    @DisplayName("InHour greater than OutHour Exception")
    public void Given_inTimeGreaterThanOutTimeParking_When_calculateFare_Then_throwsIllegalArgumentException() {
        Instant inTime = Instant.EPOCH.plusMillis(60 * 60 * 1000); // inTime is one hour greater than outTime.
        Instant outTime = Instant.EPOCH;
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
    @DisplayName("Equivalence fare/parkingDuration : three quarter bike hour")
    public void Given_threeQuarterOfHourBikeParking_When_calculateFare_Then_priceEqualTo75PercentOfBikeHourRate() {
        // use fareCalculatorService.formatFare() to keep only two decimals on expectedFare.
        double expectedFare = FareCalculatorService.formatFare(0.75 * ParkingType.BIKE.getFare());
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(45 * 60 * 1000); //45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.BIKE, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(expectedFare, ticket.getPrice());
    }

    /**
     * Check consistency between car hour rate and three quarter of hour parking fare.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : three quarter car hour")
    public void Given_threeQuarterOfHourCarParking_When_calculateFare_Then_priceEqualTo75PercentOfCarHourRate() {
        // use fareCalculatorService.formatFare() to keep only two decimals on expectedFare.
        double expectedFare = FareCalculatorService.formatFare(0.75 * ParkingType.CAR.getFare());
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(45 * 60 * 1000); //45 minutes parking time should give 3/4th parking fare
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(expectedFare, ticket.getPrice());
    }

    /**
     * Check consistency between car hour rate and one day parking fare.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : one car day")
    public void Given_oneDayParking_When_calculateFare_Then_priceEqualTo24TimesHourRate() {
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(24 * 60 * 60 * 1000); //24 hours parking time should give 24 * parking fare per hour
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        fareCalculatorService.calculateFare(ticket);
        assertEquals((24 * ParkingType.CAR.getFare()), ticket.getPrice());
    }

    /**
     * Check consistency between previous saved ticket and discount on new ticket.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : one discounted car hour")
    public void Given_previousTicketForCarUser_When_calculateFare_Then_ticketPriceDiscountedBy5Percent() {
        // use fareCalculatorService.formatFare() to keep only two decimals on expectedFare.
        double discountedFare = FareCalculatorService.formatFare(0.95 * ParkingType.CAR.getFare());

        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(60 * 60 * 1000); // 60 minutes parking.
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscounted(true);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(discountedFare, ticket.getPrice());
    }

    /**
     * Check free fare under 30 minutes parking.
     */
    @Test
    @DisplayName("Equivalence fare/parkingDuration : free under thirty minutes.")
    public void Given_lessThan30MinutesParking_When_calculateFare_Then_freeFare() {
        Instant inTime = Instant.EPOCH;
        Instant outTime = Instant.EPOCH.plusMillis(29 * 60 * 1000); // Test on 29 minutes parking
        ParkingSpot parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);

        ticket.setInTime(inTime);
        ticket.setOutTime(outTime);
        ticket.setParkingSpot(parkingSpot);
        ticket.setDiscounted(false);
        fareCalculatorService.calculateFare(ticket);
        assertEquals(0, ticket.getPrice());
    }
}
