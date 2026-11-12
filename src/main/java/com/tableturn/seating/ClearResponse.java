package com.tableturn.seating;

import java.time.Instant;

/** POST /seatings/{seatingId}/clear 200: the table is free again, and the turn the party had on it. */
public record ClearResponse(String seatingId, String table, int turnMinutes, Instant clearedAt) {
}