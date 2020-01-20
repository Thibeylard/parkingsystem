package com.parkit.parkingsystem;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.service.ParkingService;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;

import org.mockito.Mock;
import org.mockito.Mockito;

import static org.mockito.ArgumentMatchers.anyString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.when;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.any;

@ExtendWith(MockitoExtension.class)
public class ParkingServiceTest {

    /**
     * Class Under Test.
     */
    private static ParkingService parkingService;
    /**
     * inputReaderUtil mock : simulate user entries.
     */
    @Mock
    private static InputReaderUtil inputReaderUtil;
    /**
     * parkingSpotDAO mock : simulate database access to ParkingSpot table.
     */
    @Mock
    private static ParkingSpotDAO parkingSpotDAO;
    /**
     * ticketDAO mock : simulate database access to Ticket table.
     */
    @Mock
    private static TicketDAO ticketDAO;
    /**
     * Predefined value for regNumber values.
     */
    private static final String regNumber = "ABCDEF";

    /**
     * Initialize theoretical situation for tests.
     */
    @BeforeEach
    private void setUpPerTest() {
        ParkingServiceTest.parkingService = new ParkingService(inputReaderUtil, parkingSpotDAO, ticketDAO);
    }

    /**
     * Check if processIncomingVehicleTest has really called DAOs methods.
     * @throws Exception for readVehicleRegistrationNumber()
     */
    @Test
    public void Given_anyVehicle_When_enterParking_Then_callDAOMethods() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);

        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processIncomingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
        verify(parkingSpotDAO, Mockito.times(1)).getNextAvailableSlot(ParkingType.CAR);
        verify(ticketDAO, Mockito.times(1)).getTicket(regNumber);
        verify(ticketDAO, Mockito.times(1)).saveTicket(any(Ticket.class));
    }

    /**
     * Check if processIncomingVehicleTest has really called DAOs methods.
     */
    @Test
    public void Given_fullParking_When_enterParking_Then_noTicketSaved() {
        when(inputReaderUtil.readSelection()).thenReturn(1);

        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(-1);

        parkingService.processIncomingVehicle();
        verify(ticketDAO, Mockito.times(0)).saveTicket(any(Ticket.class));
    }

    /**
     *
     */
    @Test
    public void Given_recurringUser_When_enterParking_Then_discountTicket() throws Exception {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);

        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);

        Ticket oldTicket = new Ticket();
        when(ticketDAO.getTicket(regNumber)).thenReturn(oldTicket);
        when(ticketDAO.saveTicket(any(Ticket.class))).thenReturn(true);

        parkingService.processIncomingVehicle();

        ArgumentCaptor<Ticket> newTicket = ArgumentCaptor.forClass(Ticket.class);
        verify(ticketDAO).saveTicket(newTicket.capture());

        assertTrue(newTicket.getValue().isDiscounted());
    }

    /**
     * Check if processExitingVehicleTest has really called parkingSpotDAO.updateParking().
     */
    @Test
    public void Given_anyParkedVehicle_When_exitParking_Then_callParkingSpotDAOUpdateParkingMethod() {
        ParkingSpot parkingSpot;
        try {
            when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
            parkingSpot = new ParkingSpot(1, ParkingType.CAR, false);
            // ticket is spied in order to simulate outTime over real value : it is defined in processExitingVehicle() as Instant.now, and that we don't want.
            Ticket ticket = spy(new Ticket());
            ticket.setInTime(Instant.EPOCH);
            Instant mockedOutTime = Instant.EPOCH.plusMillis(60 * 60 * 1000); // ticket outTime that will be returned by ticket.getOutTime()
            ticket.setParkingSpot(parkingSpot);
            ticket.setVehicleRegNumber(regNumber);

            when(ticket.getOutTime()).thenReturn(mockedOutTime, mockedOutTime, mockedOutTime); // ticket.getOutTime() is called three times in fareCalculatorService.
            when(ticketDAO.getTicket(anyString())).thenReturn(ticket);
            when(ticketDAO.updateTicket(any(Ticket.class))).thenReturn(true);
            when(parkingSpotDAO.updateParking(any(ParkingSpot.class))).thenReturn(true);
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Failed to set up test objects");
        }
        parkingService.processExitingVehicle();
        verify(parkingSpotDAO, Mockito.times(1)).updateParking(any(ParkingSpot.class));
    }

    /**
     * Check ParkingType.CAR user entry when vehicle type asked returns correct ParkingSpot.
     */
    @Test
    public void Given_userFillCarType_When_getVehicleType_Then_returnParkingSpot1() {
        when(inputReaderUtil.readSelection()).thenReturn(1);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.CAR)).thenReturn(1);
        assertEquals(new ParkingSpot(1, ParkingType.CAR, true), parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * Check ParkingType.BIKE user entry when vehicle type asked returns correct ParkingSpot.
     */
    @Test
    public void Given_userFillBikeType_When_getVehicleType_Then_returnParkingSpot3() {
        when(inputReaderUtil.readSelection()).thenReturn(2);
        when(parkingSpotDAO.getNextAvailableSlot(ParkingType.BIKE)).thenReturn(3);
        assertEquals(new ParkingSpot(3, ParkingType.CAR, true), parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * Check wrong user entry when vehicle type asked returns no ParkingSpot.
     */
    @Test
    public void Given_userFillWrongParkingType_When_getVehicleType_Then_throwsIllegalArgumentException() {
        when(inputReaderUtil.readSelection()).thenReturn(13);
        assertNull(parkingService.getNextParkingNumberIfAvailable());
    }

    /**
     * Check wrong user entry when vehicle type asked.
     * @throws Exception for readVehicleRegistrationNumber()
     */
    @Test
    public void Given_noTicketForFilledRegNumber_When_exitVehicle_Then_abortExitingWithoutUpdatingTicket() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        when(ticketDAO.getTicket(regNumber)).thenReturn(null);
        parkingService.processExitingVehicle();
        verify(ticketDAO,Mockito.times(0)).updateTicket(any(Ticket.class));
    }

    /**
     * Check wrong user entry when vehicle type asked.
     * @throws Exception for readVehicleRegistrationNumber()
     */
    @Test
    public void Given_errorOnUpdate_When_exitVehicle_Then_abortExitingWithoutAskingPrice() throws Exception {
        when(inputReaderUtil.readVehicleRegistrationNumber()).thenReturn(regNumber);
        Ticket ticket = spy(new Ticket());
        ticket.setInTime(Instant.EPOCH);
        Instant mockedOutTime = Instant.EPOCH.plusMillis(60 * 60 * 1000); // ticket outTime that will be returned by ticket.getOutTime()
        ticket.setParkingSpot(new ParkingSpot(1, ParkingType.CAR, false));
        ticket.setVehicleRegNumber(regNumber);

        when(ticket.getOutTime()).thenReturn(mockedOutTime, mockedOutTime, mockedOutTime); // ticket.getOutTime() is called three times in fareCalculatorService.
        when(ticketDAO.getTicket(regNumber)).thenReturn(ticket);
        when(ticketDAO.updateTicket(ticket)).thenReturn(false);

        parkingService.processExitingVehicle();
    }
}
