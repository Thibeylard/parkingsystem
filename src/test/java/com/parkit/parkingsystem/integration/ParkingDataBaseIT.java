package com.parkit.parkingsystem.integration;

import com.parkit.parkingsystem.constants.Fare;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.integration.service.DataBasePrepareService;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    /**
     * dataBaseConfig for tests access
     */
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    /**
     * dataBasePrepareService for tests access
     */
    private static DataBasePrepareService dataBasePrepareService;
    /**
     * Real ParkingSpotDAO instance
     */
    private static ParkingSpotDAO parkingSpotDAO;
    /**
     * Real TicketDAO instance
     */
    private static TicketDAO ticketDAO;
    /**
     * Predefined value for regNumber values.
     */
    private final String regNumber = "ABCDEF";

    /**
     * Mock for user entries.
     */
    @Mock
    private static InputReaderUtil inputReaderUtil;

    /**
     * Global set up for integration tests
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
     * @throws Exception in case of read entry fail
     */
    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(this.regNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    /**
     * Check consistency of Ticket and ParkingSpot database tables save.
     */
    @Test
    public void Given_parkingSpot1Available_When_userEntersWithCar_Then_ticketSavedAndSpotUnavailable() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        assertSame(ticketDAO.getTicket(this.regNumber).getClass(), Ticket.class);
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)); // ParkingSpot n°1 is supposed to be unavailable. nextAvailableSpot expected is n°2.
    }

    /**
     * Check consistency of Ticket database table price and outTime values.
     * @throws Exception in case readVehicleRegistrationNumber fails
     */
    @Test
    public void Given_parkedForOneHour_When_userLeaves_Then_fareAndOutTimeAreCoherent() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(this.regNumber); // Registration number is read once more in this method : On entrance then on exit.
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(this.regNumber);
        dataBasePrepareService.antedateTicket(ticket, 3600000);
        parkingService.processExitingVehicle();
        ticket = ticketDAO.getTicket(this.regNumber);
        assertEquals(ticket.getInTime().plusMillis(3600000).truncatedTo(ChronoUnit.MINUTES), ticket.getOutTime());
        assertEquals((ticket.getOutTime().minusMillis(ticket.getInTime().toEpochMilli()).toEpochMilli() / 3600000.) * Fare.CAR_RATE_PER_HOUR, ticket.getPrice());
    }


}
