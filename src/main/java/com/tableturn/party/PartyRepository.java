package com.tableturn.party;

import java.time.Instant;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
import org.springframework.stereotype.Repository;

/** The parties table. */
@Repository
                        public class PartyRepository {

    /**
     * One party as the screens show it (the phone number is never shown); waitedMinutes and the table it sits at are
     * null until it is seated.
     */
    public record Row(long id, String name, int covers, String state, int position, Instant joinedAt,
                      int quotedMinutes, Instant quotedAt, Integer waitedMinutes, String table) {
    }

    private final JdbcClient jdbc;

    public PartyRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** A new waiting party, quoted at the moment it joins.*/
    public long insert(long branchId, String name, int covers, String phone,
                       Instant joinedAt, int quotedMinutes) {

        KeyHolder key = new GeneratedKeyHolder();
        jdbc.sql("""
                INSERT INTO parties (branch_id, name, covers, phone, state, joined_at, position,
                        quoted_minutes, quoted_at)
                SELECT ?, ?, ?, ?, 'waiting', ?, 1 + COUNT(*), ?, ?
                FROM parties WHERE branch_id = ? AND state = 'waiting'""")
                .params(branchId, name, covers, phone, joinedAt,
                        quotedMinutes, joinedAt, branchId)
                .update(key, "id");
return key.getKeyAs(Long.class);
    }
public Optional<Row> find(long branchId, long id) {
        return jdbc.sql("""
                        SELECT p.id, p.name, p.covers, p.state, p.position, p.joined_at, p.quoted_minutes,
                               p.quoted_at, p.waited_minutes, t.no AS "table"
                        FROM parties p
                        LEFT JOIN seatings s ON s.party_id = p.id
                        LEFT JOIN tables t ON t.id = s.table_id
                        WHERE p.branch_id = ? AND p.id = ?""")
                .params(branchId, id)
                .query(Row.class)
                .optional();
    }

                public void markSeated(long id) {
        jdbc.sql("UPDATE parties SET state = 'seated' WHERE id = ?")
                .param(id).update();
    }

    /** The real wait, written once when the party is seated. */
    public void stampWait(long id, int waitedMinutes) {
        jdbc.sql("UPDATE parties SET waited_minutes = ? WHERE id = ?")
                .params(waitedMinutes, id).update();
    }
}