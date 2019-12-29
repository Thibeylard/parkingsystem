package com.parkit.parkingsystem.constants;

public final class DBConstants {

    /**
     * Find minimal available parkingNumber on vehicle entrance.
     */
    public static final String GET_NEXT_PARKING_SPOT =
        "select min(PARKING_NUMBER) from parking where AVAILABLE = true and TYPE = ?";
    /**
     * Set parkingSpot available or not available respectively on vehicle out or in.
     */
    public static final String UPDATE_PARKING_SPOT =
        "update parking set available = ? where PARKING_NUMBER = ?";
    /**
     * Insert new ticket row on vehicle entrance.
     */
    public static final String SAVE_TICKET =
        "insert into ticket(PARKING_NUMBER, VEHICLE_REG_NUMBER, PRICE, IN_TIME, OUT_TIME) values(?,?,?,?,?)";
    /**
     * Update ticket price and outTime on vehicle exit.
     */
    public static final String UPDATE_TICKET =
        "update ticket set PRICE=?, OUT_TIME=? where ID=?";
    /**
     * Get specific ticket on vehicle exit.
     */
    public static final String GET_TICKET =
        "select t.PARKING_NUMBER, t.ID, t.PRICE, t.IN_TIME, t.OUT_TIME, p.TYPE from ticket t,parking p where p.parking_number = t.parking_number and t.VEHICLE_REG_NUMBER=? order by t.IN_TIME  limit 1";

    private DBConstants() { };
}
