package com.tableturn.seating;

import jakarta.validation.constraints.NotBlank;

/** POST /seatings: which party, at which table.*/
public record SeatRequest(@NotBlank String partyId, @NotBlank String table) {
}