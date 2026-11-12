package com.tableturn.seating;

import java.time.Instant;

/** POST /seatings 201: the new seating and the real wait stamped on the party. */
public record SeatingResponse(String seatingId, String partyId, String table, Instant seatedAt, int waitedMinutes) {
}