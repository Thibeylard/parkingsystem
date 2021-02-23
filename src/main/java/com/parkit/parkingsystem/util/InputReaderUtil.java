package com.parkit.parkingsystem.util;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Scanner;

public class InputReaderUtil {

    /**
     * InputReaderUtil class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("InputReaderUtil");
    /**
     * User shell inputs scanner member attribute.
     */
    private static Scanner scan = new Scanner(System.in);

    /**
     * Get integer from scan member attribute nextLine().
     * @return user shell entry as Integer
     */
    public int readSelection() {
        try {
            return Integer.parseInt(scan.nextLine());
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter valid number for proceeding further");
            return -1;
        }
    }

    /**
     * Get vehicleRegistrationNumber from scan member attribute nextLine().
     * @return validated user registration number
     * @throws Exception IllegalArgumentException for wrong registration number format
     */
    public String readVehicleRegistrationNumber() throws Exception {
        try {
            String vehicleRegNumber = scan.nextLine();
            if (vehicleRegNumber == null || vehicleRegNumber.trim().length() == 0) {
                throw new IllegalArgumentException("Invalid input provided");
            }
            return vehicleRegNumber;
        } catch (Exception e) {
            LOGGER.error("Error while reading user input from Shell", e);
            System.out.println("Error reading input. Please enter a valid string for vehicle registration number");
            throw e;
        }
    }


}
