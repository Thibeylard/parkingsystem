package com.parkit.parkingsystem.integration.service;

import com.parkit.parkingsystem.integration.config.DataBaseTestConfig;
import com.parkit.parkingsystem.model.Ticket;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.Timestamp;
import java.time.Instant;

public class DataBasePrepareService {

    /**
     * Integration package DataBaseConfig.
     */
    private DataBaseTestConfig dataBaseTestConfig = new DataBaseTestConfig();

    /**
     * Reset Parking and Ticket tables.
     */
    public void clearDataBaseEntries() {
        Connection connection = null;
        try {
            connection = dataBaseTestConfig.getConnection();

            //set parking entries to available
            connection.prepareStatement("update parking set available = true").execute();

            //clear ticket entries;
            connection.prepareStatement("truncate table ticket").execute();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataBaseTestConfig.closeConnection(connection);
        }
    }

    /**
     * Used to antedate a ticket for reproductibility purpose
     * @param ticket ticket to antedate
     * @param msToAntedate Number of milliseconds to antedate
     * @return true if operation succeeded
     */
    public boolean antedateTicket(final Ticket ticket, final long msToAntedate) {
        Connection con = null;
        try {
            con = dataBaseTestConfig.getConnection();
            PreparedStatement ps = con.prepareStatement("update ticket set IN_TIME=? where ID=?");
            ps.setTimestamp(1, new Timestamp(Instant.now().minusMillis(msToAntedate).toEpochMilli()));
            ps.setInt(2, ticket.getId());
            ps.execute();
            return true;
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            dataBaseTestConfig.closeConnection(con);
        }
        return false;
    }
}
