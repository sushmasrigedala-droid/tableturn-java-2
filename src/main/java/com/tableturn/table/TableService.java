package com.tableturn.table;

import java.time.Clock;
import java.time.Duration;
import java.util.List;

import org.springframework.stereotype.Service;

import com.tableturn.error.NoSuchBranch;
import com.tableturn.error.NoSuchTable;
import com.tableturn.party.PartyIds;

/**  The floor: every table with its state, derived from its current seating on every read.  */
@Service
public class TableService {

    private final BranchRepository branches;
    private final TableRepository tables;
    private final Clock clock;

    public TableService(BranchRepository branches, TableRepository tables, Clock clock) {
      this.branches=branches;
      this.tables = tables;
      this.clock = clock;
    }
/** Returns all tables in a branch. Optionally filters by section and state.*/
    public FloorView floor(String branch,Section section,TableState state){
        List<TableView> shown = tables.all(branchId(branch)).stream()
                .map(this::view)
                .filter(t -> section == null || t.section() == section)
                .filter(t -> state == null || t.state() == state)
                .toList();  
                int free =(int) shown.stream().filter(t -> t.state() == TableState.free).count();
                return new FloorView(branch, free, shown);
    }
    /** Returns a single table view by branch and table number. */
    public TableView table(String branch, String no) {
        long branchId = branchId(branch);
       TableRepository.Row row = tables.findByNo(branchId, no)
        .orElseThrow(() -> new NoSuchTable(branch, no));
        return view(row);
    }
    /**
     * Derived on every read: free when no seating is current, seated while one is, with the
     * minutes the party has sat counted from the clock.
     */
    private TableView view(TableRepository.Row row) {
              if (row.currentSeatingId() == null) {
            return new TableView(
                row.no(),row.seats(),row.section(),TableState.free,null, null);
 }
 int seatedMinutes = (int) Duration.between(
        row.seatedAt(),clock.instant()).toMinutes();
        return new TableView(
        row.no(),row.seats(),row.section(),TableState.seated,PartyIds.format(row.partyId()),seatedMinutes);
    }
private long branchId(String branch) {
                return branches.idOf(branch)
                .orElseThrow(() -> new NoSuchBranch(branch));
    }
}