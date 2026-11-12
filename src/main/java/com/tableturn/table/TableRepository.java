package com.tableturn.table;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The tables table. */
@Repository
public class TableRepository {

    public record Row(
            long id,
            String no,
            int seats,
            Section section,
            Long currentSeatingId,
            Long partyId,
            Instant seatedAt
    ) {}

    private static final String SELECT = """
        SELECT t.id, t.no, t.seats, t.section,
               t.current_seating_id, s.party_id, s.seated_at
        FROM tables t
        LEFT JOIN seatings s ON s.id = t.current_seating_id
        WHERE t.branch_id = ?
        """;

    private final JdbcClient jdbc;

    public TableRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }

    /** Every table of a branch, lowest number first. */
    public List<Row> all(long branchId) {
        return jdbc.sql(SELECT + " ORDER BY t.no")
                .param(branchId)
                .query(Row.class)
                .list();
    }

    /** Finds a table using its branch ID and table number. */
    public Optional<Row> findByNo(long branchId, String no) {
        return jdbc.sql(SELECT + " AND t.no = ?")
                .params(branchId, no)
                .query(Row.class)
                .optional();
    }

    /** Seats of the largest table in the branch. */
    public int largest(long branchId) {
        return jdbc.sql(
                "SELECT MAX(seats) FROM tables WHERE branch_id = ?"
        )
                .param(branchId)
                .query(Integer.class)
                .single();
    }

    /** The table now holds this seating. */
    public void seat(long tableId, long seatingId) {
        jdbc.sql(
                "UPDATE tables SET current_seating_id = ? WHERE id = ?"
        )
                .params(seatingId, tableId)
                .update();
    }

    /** Inserts a new table. */
    public void insert(long branchId, String no, int seats, Section section) {
        jdbc.sql("""
                INSERT INTO tables (branch_id, no, seats, section)
                VALUES (?, ?, ?, ?)
                """)
                .params(branchId, no, seats, section.name())
                .update();
    }
}