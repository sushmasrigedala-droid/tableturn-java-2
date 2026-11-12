package com.tableturn.seating;

import java.time.Instant;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** The seatings table. */
@Repository
public class SeatingRepository {

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
}