package com.tableturn.seating;

import java.time.Instant;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** The seatings table. */
@Repository
public class SeatingRepository {

/** One seating, with the party and table it joins (their database ids, and the table's number). */
    public record Row( long id,long partyId,long tableId,String table,Instant seatedAt,Instant leftAt,
    Instant clearedAt) {
    }

    private final JdbcClient jdbc;

        public SeatingRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** A new seating of this party at this table; its database id. */
    public long insert(long branchId, long partyId, long tableId, Instant seatedAt) {
        KeyHolder key = new GeneratedKeyHolder();
            jdbc.sql("INSERT INTO seatings (branch_id, party_id, table_id, seated_at) VALUES (?, ?, ?, ?)")
                .params(branchId, partyId, tableId, seatedAt) .update(key, "id");
return key.getKeyAs(Long.class);
    }

    public Optional<Row> find(long branchId, long id) {
        return jdbc.sql("""
                        SELECT s.id, s.party_id, s.table_id, t.no AS "table", s.seated_at, s.left_at, s.cleared_at
                        FROM seatings s JOIN tables t ON t.id = s.table_id
                       WHERE s.branch_id = ? AND s.id = ? """ )
                .params(branchId, id) .query(Row.class).optional();
    }

    public void leave(long id, Instant leftAt) {
        jdbc.sql("UPDATE seatings SET left_at = ? WHERE id = ?").params(leftAt, id) .update();
    }

        public void clear(long id, Instant clearedAt, int turnMinutes) {
        jdbc.sql("UPDATE seatings SET cleared_at = ?, turn_minutes = ? WHERE id = ?")
                .params(clearedAt, turnMinutes, id) .update();
    }
}