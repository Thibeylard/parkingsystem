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

import static java.lang.Thread.sleep;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class ParkingDataBaseIT {

    /**
     * dataBaseConfig from integration package.
     */
    private static DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();
    /**
     *
     */
    private static ParkingSpotDAO parkingSpotDAO;
    private static TicketDAO ticketDAO;
    private static DataBasePrepareService dataBasePrepareService;
    private final String regNumber = "ABCDEF";

    @Mock
    private static InputReaderUtil inputReaderUtil;

    @BeforeAll
    private static void setUp() throws Exception {
        parkingSpotDAO = new ParkingSpotDAO();
        parkingSpotDAO.setDataBaseConfig(dataBaseTestConfig);
        ticketDAO = new TicketDAO();
        ticketDAO.setDataBaseConfig(dataBaseTestConfig);
        dataBasePrepareService = new DataBasePrepareService();
    }

    @BeforeEach
    private void setUpPerTest() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(this.regNumber).thenReturn(this.regNumber);
        dataBasePrepareService.clearDataBaseEntries();
    }

    @AfterAll
    private static void tearDown() {

    }

    private void carEntersParking() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
    }

    @Test
    public void Given_noTicket_When_userEntersWithCar_Then_ticketSavedInDatabase() {
        carEntersParking();
        assertTrue(ticketDAO.getTicket(this.regNumber).getClass() == Ticket.class);
    }

    @Test
    public void Given_parkingSpot1Available_When_userEntersWithCar_Then_nextParkingSpotIs2() {
        carEntersParking();
        assertEquals(2, parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)); // ParkingSpot n°1 is supposed to be unavailable. nextAvailableSpot expected is n°2.
    }


    private Ticket carLeavesParking() {
        ParkingService parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
        parkingService.processIncomingVehicle();
        Ticket ticket = ticketDAO.getTicket(this.regNumber);
        dataBasePrepareService.antedateTicket(ticket,3600000);
        parkingService.processExitingVehicle();
        return ticketDAO.getTicket(this.regNumber);
    }

    @Test
    public void Given_inTimeInstant_When_userLeavesWithCarAfterOneHour_Then_outTimeIsInTimePlusOneHour() {
        Ticket ticket = carLeavesParking();
        assertEquals(ticket.getInTime().plusMillis(3600000).truncatedTo(ChronoUnit.MINUTES),ticket.getOutTime());
    }

    @Test
    public void Given_parkedForOneHour_When_userLeavesWithCar_Then_fareEqualsOneHourParking() {
        Ticket ticket = carLeavesParking();
        assertEquals((ticket.getOutTime().minusMillis(ticket.getInTime().toEpochMilli()).toEpochMilli() / 3600000.) * Fare.CAR_RATE_PER_HOUR,ticket.getPrice());
    }


}
