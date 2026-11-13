package com.tableturn.party;

import java.time.Instant;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

/**
 * GET /parties/{partyId}: the quote, and the real wait and the table once the party is seated (both absent
 * while it waits); its timeline, oldest first.
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public record PartyView(String partyId, String name, int covers, String state, int position, Instant joinedAt,
                        int quotedMinutes, Instant quotedAt, Integer waitedMinutes, String table,
                        List<EventView> events) {
}