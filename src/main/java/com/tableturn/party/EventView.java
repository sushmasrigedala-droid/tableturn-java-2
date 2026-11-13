package com.tableturn.party;

import java.time.Instant;
import java.util.Map;

import com.fasterxml.jackson.annotation.JsonAnyGetter;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;

/** 
 * One line of a party's timeline: when, what, and that event's own facts (the covers, the quote, the table and the
 * wait, the turn), which appear next to `at` and `kind` in the JSON.
 */
@JsonPropertyOrder({"at", "kind"})
public record EventView(Instant at, String kind, @JsonAnyGetter Map<String, Object> facts) {
}



