package com.tableturn.seating;

/** A seating's public id - S001, S204 - made from its database id, which is never shown itself.*/
public final class SeatingIds {

    private SeatingIds() {
    }

    public static String format(long id) {
        return String.format("S%03d", id);
    }
}