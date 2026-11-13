package com.tableturn.party;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The append-only log. It can add an event and read a party's events - nothing else, so nothing in the app can 
 * change or remove one. Called inside the transaction of the change it records, so a rolled-back change leaves no
 * event behind. The facts are ids and numbers, never the guest's phone.
 */
@Repository
public class EventLog {

    private record Row(Instant at, String kind, String data) {
    }

    private final JdbcClient jdbc;
    private final ObjectMapper json;

    public EventLog(JdbcClient jdbc, ObjectMapper json) {
        this.jdbc = jdbc;
        this.json = json;
    }

        /** One event; seatingId is null for the events before a seating exists (joined, quoted). */
    public void append(long branchId, long partyId, Long seatingId, String kind, Instant at,
                       Map<String, Object> facts) {
        jdbc.sql("INSERT INTO events (branch_id, party_id, seating_id, kind, at, data) VALUES (?, ?, ?, ?, ?, ?)")
                .params(branchId, partyId, seatingId, kind, at, write(facts)).update();
    }

        /** A party's events, oldest first. */
    public List<EventView> forParty(long partyId) {
                return jdbc.sql("SELECT at, kind, data FROM events WHERE party_id = ? ORDER BY at, id").param(partyId)
                .query(Row.class).list().stream()
                .map(r -> new EventView(r.at(), r.kind(), read(r.data()))).toList();
    }

        private String write(Map<String, Object> facts) {
        try {
            return json.writeValueAsString(facts);
                  } catch (JsonProcessingException e) {
            throw new IllegalArgumentException("event facts must be JSON", e);
        }
    }

           private Map<String, Object> read(String data) {
        try {
            return json.readValue(data, new TypeReference<Map<String, Object>>() { });
                } catch (JsonProcessingException e) {
            throw new IllegalStateException("an events row holds data that is not JSON", e);
        }
    }
}