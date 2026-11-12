package com.tableturn.seating;

import java.time.Instant;

/** POST /seatings/{seatingId}/leave 200: the moment the party left. */
public record LeaveResponse(String seatingId, Instant leftAt) {
}