package com.parkit.parkingsystem.dao;

import com.parkit.parkingsystem.config.DataBaseConfig;
import com.parkit.parkingsystem.constants.DBConstants;
import com.parkit.parkingsystem.constants.ParkingType;
import com.parkit.parkingsystem.model.ParkingSpot;
import com.parkit.parkingsystem.model.Ticket;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.time.temporal.ChronoUnit;

public class TicketDAO {

    /**
     * TicketDAO class logger.
     */
    private static final Logger LOGGER = LogManager.getLogger("TicketDAO");

    /**
     * Database Configuration object.
     */
    private DataBaseConfig dataBaseConfig = new DataBaseConfig();

    /**
     * Save ticket object to Ticket table.
     * @param ticket object to save
     * @return true if operation succeeded
     */
    public boolean saveTicket(final Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.SAVE_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME)
            //ps.setInt(1,ticket.getId());
            ps.setInt(1, ticket.getParkingSpot().getId());
            ps.setString(2, ticket.getVehicleRegNumber());
            ps.setDouble(3, ticket.getPrice());
            ps.setTimestamp(4, new Timestamp(ticket.getInTime().toEpochMilli()));
            ps.setTimestamp(5, (ticket.getOutTime() == null) ? null : (new Timestamp(ticket.getOutTime().toEpochMilli())));
            ps.setBoolean(6,ticket.isDiscounted());
            return ps.execute();
        } catch (Exception ex) {
            LOGGER.error("Error fetching next available slot", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return false;
        }
    }

    /**
     * Get ticket object from Ticket table.
     * @param vehicleRegNumber used to retrieve ticket
     * @return ticket object
     */
    public Ticket getTicket(final String vehicleRegNumber) {
        Connection con = null;
        Ticket ticket = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.GET_TICKET);
            //ID, PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME, DISCOUNT)
            ps.setString(1, vehicleRegNumber);
            ResultSet rs = ps.executeQuery();
            if (rs.next()) {
                ticket = new Ticket();
                ParkingSpot parkingSpot = new ParkingSpot(rs.getInt(1), ParkingType.valueOf(rs.getString(7)), false);
                ticket.setParkingSpot(parkingSpot);
                ticket.setId(rs.getInt(2));
                ticket.setVehicleRegNumber(vehicleRegNumber);
                ticket.setPrice(rs.getDouble(3));
                ticket.setInTime(rs.getTimestamp(4).toInstant().truncatedTo(ChronoUnit.MINUTES));
                // In most cases, ticket is needed when not already closed. Must check value to avoid nullPointerException
                if (rs.getTimestamp(5) != null) {

                    ticket.setOutTime(rs.getTimestamp(5).toInstant().truncatedTo(ChronoUnit.MINUTES));
                }
                ticket.setDiscounted(rs.getBoolean(6));
            }
            dataBaseConfig.closeResultSet(rs);
            dataBaseConfig.closePreparedStatement(ps);
        } catch (Exception ex) {
            LOGGER.error("Error fetching ticket", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
            return ticket;
        }
    }

    /**
     * Modify ticket in Ticket table.
     * @param ticket object to modify
     * @return true if operation succeeded
     */
    public boolean updateTicket(final Ticket ticket) {
        Connection con = null;
        try {
            con = dataBaseConfig.getConnection();
            PreparedStatement ps = con.prepareStatement(DBConstants.UPDATE_TICKET);
            ps.setDouble(1, ticket.getPrice());
            ps.setTimestamp(2, new Timestamp(ticket.getOutTime().toEpochMilli()));
            ps.setInt(3, ticket.getId());
            ps.execute();
            return true;
        } catch (Exception ex) {
            LOGGER.error("Error saving ticket info", ex);
        } finally {
            dataBaseConfig.closeConnection(con);
        }
        return false;
    }

    /**
     * Set dataBaseConfig member attribute (used in ParkingDataBaseIT).
     * @param dataBaseConfigToSet new dataBaseConfig to set
     */
    public void setDataBaseConfig(final DataBaseConfig dataBaseConfigToSet) {
        this.dataBaseConfig = dataBaseConfigToSet;
    }
}
