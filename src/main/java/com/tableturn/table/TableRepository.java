package com.tableturn.table;

import java.time.Instant;
import java.util.List;

import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.stereotype.Repository;

/** The tables in a branch. */

@Repository
public class TableRepository {

/**
     * One tables row: its number, its seats, its section and the seating on it - with that seating's party, the
     * moment it sat down and the moment it left (all null when the table has no seating).
     */
    public record Row(long id,String table,int seats,String section,Long seatingId,Long partyId, Instant seatedAt, Instant leftAt ) {
    }
    private final JdbcClient jdbc;

    public TableRepository(JdbcClient jdbc) {
        this.jdbc = jdbc;
    }
    /** Insert a new table into a branch. */
public void insert(long branchId, String tableNo, int seats, Section section) {
    jdbc.sql("""
            INSERT INTO tables (branch_id, no, seats, section)
            VALUES (?, ?, ?, ?)
            """)
            .params(branchId, tableNo, seats, section.name())
            .update();
}

    /** Find all tables in a branch and their current seating information. */
    public List<Row> findAll(long branchId) {
        return jdbc.sql("""
                SELECT t.id,t.no AS "table",t.seats, t.section, s.id AS seating_id,s.party_id,
                 s.seated_at,s.left_at
                FROM tables t
                LEFT JOIN seatings s
                    ON s.table_id = t.id
                    AND s.cleared_at IS NULL
                WHERE t.branch_id = ?
                ORDER BY t.no
                """)
                .params(branchId)
                .query(Row.class)
                .list();
    }
        /** Find the maximum seating capacity available in a branch. */
    public int largest(long branchId) {
        return jdbc.sql("SELECT COALESCE(MAX(seats), 0) FROM tables WHERE branch_id = ?")
                .params(branchId)
                .query(Integer.class)
                .single();
    }
    /** Mark a table as occupied by a seating. */
public void seat(long tableId, long seatingId) {
    jdbc.sql("""
            UPDATE tables
            SET current_seating_id = ?
            WHERE id = ?
            """)
            .params(seatingId, tableId)
            .update();
}
        /** Find a table by its number within a branch. */
    public java.util.Optional<Row> findByNo(long branchId, String tableNo) {
        return jdbc.sql("""
                SELECT t.id, t.no AS "table", t.seats, t.section,
                       s.id AS seating_id, s.party_id,
                       s.seated_at, s.left_at
                FROM tables t
                LEFT JOIN seatings s
                    ON s.table_id = t.id
                    AND s.cleared_at IS NULL
                WHERE t.branch_id = ?
                  AND t.no = ?
                """)
                .params(branchId, tableNo)
                .query(Row.class)
                .optional();
    }
    /** Cleared: the table holds no seating again. */
    public void clear(long tableId, Instant clearedAt) {
        jdbc.sql("UPDATE tables SET current_seating_id = NULL, cleared_at = ? WHERE id = ?")
    .params(clearedAt, tableId)
    .update();
    }
}