package com.parkit.parkingsystem.service;

import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.dao.ParkingSpotDAO;
import com.parkit.parkingsystem.dao.TicketDAO;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import com.parkit.parkingsystem.util.InputReaderUtil;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

public class ParkingService {

    /**
     * ParkingService class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("ParkingService");

    /**
     * FareCalculatorService global implementation.
     */
    private static FareCalculatorService fareCalculatorService = new FareCalculatorService();

    /**
     * inputReaderUtil member attribute.
     */
    private InputReaderUtil inputReaderUtil;
    /**
     * parkingSpotDAO member attribute.
     */
    private ParkingSpotDAO parkingSpotDAO;
    /**
     * ticketDAO member attribute.
     */
    private TicketDAO ticketDAO;

    /**
     * Public constructor initializing inputReader and DAOs.
     *
     * @param inputReaderUtilTmp passed from InteractiveShell
     * @param parkingSpotDAOTmp  passed from InteractiveShell
     * @param ticketDAOTmp       passed from InteractiveShell
     */
    public ParkingService(final InputReaderUtil inputReaderUtilTmp, final ParkingSpotDAO parkingSpotDAOTmp, final TicketDAO ticketDAOTmp) {
        this.inputReaderUtil = inputReaderUtilTmp;
        this.parkingSpotDAO = parkingSpotDAOTmp;
        this.ticketDAO = ticketDAOTmp;
    }

    /**
     * Start registration process for an incoming vehicle.
     */
    public void processIncomingVehicle() {
        try {
            ParkingSpot parkingSpot = getNextParkingNumberIfAvailable();
            if (parkingSpot != null && parkingSpot.getId() > 0) {
                String vehicleRegNumber = getVehicleRegNumber();
                parkingSpot.setAvailable(false);
                parkingSpotDAO.updateParking(parkingSpot); //allot this parking space and mark it's availability as false

                Instant inTime = Instant.now().truncatedTo(ChronoUnit.MINUTES); // Seconds and milliseconds are not relevant
                Ticket ticket = new Ticket();
                //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
                ticket.setParkingSpot(parkingSpot);
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(0);
                ticket.setInTime(inTime);
                if (ticketDAO.getTicket(vehicleRegNumber) != null) { // Check for previous ticket
                    System.out.println("Welcome back! As a recurring user of our parking lot, you'll benefit from a 5% discount.");
                    ticket.setDiscounted(true); // Save ticket as discounted
                }
                ticketDAO.saveTicket(ticket);
                System.out.println("Generated Ticket and saved in DB");
                System.out.println("Please park your vehicle in spot number:" + parkingSpot.getId());
                System.out.println("Recorded in-time for vehicle number:" + vehicleRegNumber + " is:" + inTime);
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process incoming vehicle", e);
        }
    }

    /**
     * Get vehicle registration number from user.
     *
     * @return Valid vehicle registration number string
     * @throws Exception IllegalArgumentException for wrong registration number format
     */
    private String getVehicleRegNumber() throws Exception {
        System.out.println("Please type the vehicle registration number and press enter key");
        return inputReaderUtil.readVehicleRegistrationNumber();
    }

    /**
     * @return available ParkingSpot for user vehicle.
     */
    public ParkingSpot getNextParkingNumberIfAvailable() {
        int parkingNumber = 0;
        ParkingSpot parkingSpot = null;
        try {
            ParkingType parkingType = getVehicleType();
            parkingNumber = parkingSpotDAO.getNextAvailableSlot(parkingType);
            if (parkingNumber > 0) {
                parkingSpot = new ParkingSpot(parkingNumber, parkingType, true);
            } else {
                throw new Exception("Error fetching parking number from DB. Parking slots might be full");
            }
        } catch (IllegalArgumentException ie) {
            LOGGER.error("Error parsing user input for type of vehicle", ie);
        } catch (Exception e) {
            LOGGER.error("Error fetching next available parking slot", e);
        }
        return parkingSpot;
    }

    /**
     * Get vehicle type from user.
     *
     * @return ParkingType
     */
    private ParkingType getVehicleType() {
        System.out.println("Please select vehicle type from menu");
        final int carEntry = 1;
        final int bikeEntry = 2;
        System.out.println("1 CAR");
        System.out.println("2 BIKE");
        int input = inputReaderUtil.readSelection();
        switch (input) {
            case carEntry:
                return ParkingType.CAR;
            case bikeEntry:
                return ParkingType.BIKE;
            default:
                System.out.println("Incorrect input provided");
                throw new IllegalArgumentException("Entered input is invalid");
        }
    }

    /**
     * Resolve user parking by asking parking fare.
     */
    public void processExitingVehicle() {
        try {
            String vehicleRegNumber = getVehicleRegNumber();
            Ticket ticket = ticketDAO.getTicket(vehicleRegNumber);
            Instant outTime = Instant.now().truncatedTo(ChronoUnit.MINUTES); // Seconds and milliseconds are not relevant
            ticket.setOutTime(outTime);
            fareCalculatorService.calculateFare(ticket);
            if (ticketDAO.updateTicket(ticket)) {
                ParkingSpot parkingSpot = ticket.getParkingSpot();
                parkingSpot.setAvailable(true);
                parkingSpotDAO.updateParking(parkingSpot);
                System.out.println("Please pay the parking fare:" + ticket.getPrice());
                System.out.println("Recorded out-time for vehicle number:" + ticket.getVehicleRegNumber() + " is:" + outTime);
            } else {
                System.out.println("Unable to update ticket information. Error occurred");
            }
        } catch (Exception e) {
            LOGGER.error("Unable to process exiting vehicle", e);
        }
    }
}
