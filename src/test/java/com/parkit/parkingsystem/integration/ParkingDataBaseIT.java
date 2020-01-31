package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.FareCalculatorService;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;


import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    /**
     * dataBaseConfig for tests access.
     */
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    /**
     * dataBasePrepareService for tests access.
     */
    private static DataBasePrepareService dataBasePrepareService;
    /**
     * Real ParkingSpotDAO instance.
     */
    private static ParkingSpotDAO parkingSpotDAO;
    /**
     * Real TicketDAO instance.
     */
    private static TicketDAO ticketDAO;
    /**
     * Predefined value for regNumber values.
     */
    private static final String regNumber = "ABCDEF";

    /**
     * Mock for user entries.
     */
    @Mock
    private static InputReaderUtil inputReaderUtil;

    /**
     * Global set up for integration tests.
     *
     * @throws Exception in case of database access fail
     */
    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    /**
     * Reinitialize mocked user entries and database for each tests.
     *
     * @throws Exception in case of read entry fail
     */
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber, regNumber); // Needs to be read twice : On entrance and exit.
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    /**
     * Check consistency of Ticket and ParkingSpot database tables save.
     */
    @Test
    @DisplayName("Incoming success story")
    public void Given_parkingSpot1Available_When_userEntersWithCar_Then_ticketSavedAndSpotUnavailable() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertSame(ticketDAO.getTicket(regNumber).getClass(), Ticket.class);
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)); // ParkingSpot n°1 is supposed to be unavailable. nextAvailableSpot expected is n°2.
    }

    /**
     * Check consistency of Ticket database table price and outTime values.
     *
     * @throws Exception in case readVehicleRegistrationNumber fails
     */
    @Test
    @DisplayName("Valid parking ticket price")
    public void Given_parkedForOneHour_When_userLeaves_Then_fareAndOutTimeAreCoherent() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(regNumber); // Get ticket created in processIncomingVehicle()
        dataBasePrepareService.antedateInTimeTicket(ticket, 60 * 60 * 1000); // Antedate to simulate parking-time
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(regNumber); // Get back ticket updated in processExitingVehicle()

        double expectedFare = FareCalculatorService.formatFare(ticket.getOutTime().minusMillis(ticket.getInTime().toEpochMilli()).toEpochMilli() / (60. * 60. * 1000.) * ParkingType.CAR.getFare());
        assertEquals(ticket.getInTime().plusMillis(60 * 60 * 1000).truncatedTo(ChronoUnit.MINUTES), ticket.getOutTime());
        assertEquals(expectedFare, ticket.getPrice());
    }

    /**
     * Check consistency of discount on recurring user ticket.
     *
     * @throws Exception in case readVehicleRegistrationNumber fails
     */
    @Test
    @DisplayName("Valid discount on ticket price")
    public void Given_recurringUser_When_userLeaves_Then_fareDiscountedBy5Percent() throws Exception {
        // Create recurring user previous ticket.
        Ticket oldTicket = new Ticket();
        oldTicket.setInTime(Instant.EPOCH);
        oldTicket.setOutTime(Instant.EPOCH.plusMillis(60 * 60 * 1000));
        oldTicket.setVehicleRegNumber(regNumber);
        oldTicket.setParkingSpot(new ParkingSpot(5, ParkingType.CAR, false));
        oldTicket.setDiscounted(false);
        ticketDAO.saveTicket(oldTicket);
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);


        // See Given_parkedForOneHour_When_userLeaves_Then_fareAndOutTimeAreCoherent for details
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(regNumber);
        dataBasePrepareService.antedateInTimeTicket(ticket, 60 * 60 * 1000);
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(regNumber);


        double expectedFare = FareCalculatorService.formatFare(ticket.getOutTime().minusMillis(ticket.getInTime().toEpochMilli()).toEpochMilli() / (60. * 60. * 1000.) * ParkingType.CAR.getFare() * 0.95);
        assertTrue(ticket.isDiscounted());
        assertEquals(expectedFare, ticket.getPrice());
    }

    /**
     * Check consistency of free 30 minutes parking.
     *
     * @throws Exception in case readVehicleRegistrationNumber fails
     */
    @Test
    @DisplayName("Free ticket for 30 minutes parking")
    public void Given_lessThan30MinutesParking_When_userLeaves_Then_fareIsFree() throws Exception {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);

        // See Given_parkedForOneHour_When_userLeaves_Then_fareAndOutTimeAreCoherent for details
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(regNumber);
        dataBasePrepareService.antedateInTimeTicket(ticket, 29 * 60 * 1000);
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(regNumber);

        assertEquals(0, ticket.getPrice());
    }
}
