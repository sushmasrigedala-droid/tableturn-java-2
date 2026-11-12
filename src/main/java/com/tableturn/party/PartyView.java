package com.tableturn.party;

import java.time.Instant;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GET /parties/{partyId}: the quote, and the real wait and the table once the party is seated (both absent
 * while it waits).
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PartyView(String id, String name, int covers, String state, int position, Instant joinedAt,
                        int quotedMinutes, Instant quotedAt, Integer waitedMinutes, String table) {
}