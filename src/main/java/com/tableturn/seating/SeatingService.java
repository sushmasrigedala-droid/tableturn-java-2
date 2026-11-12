package com.tableturn.seating;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tableturn.error.AlreadyCleared;
import com.tableturn.error.AlreadyLeft;
import com.tableturn.error.AlreadySeated;
import com.tableturn.error.NoSuchParty;
import com.tableturn.error.NoSuchSeating;
import com.tableturn.error.NoSuchTable;
import com.tableturn.error.NotLeftYet;
import com.tableturn.error.TableNotCleared;
import com.tableturn.error.TableTooSmall;
import com.tableturn.party.PartyIds;
import com.tableturn.party.PartyRepository;
import com.tableturn.party.PartyService;
import com.tableturn.table.BranchRepository;
import com.tableturn.table.TableRepository;

/**
 * Seating a party at a table, its leaving and the table's clearing. Every refusal is thrown from here and mapped 
 * once, by the exception handler.
 */
@Service
public class SeatingService {

        private final BranchRepository branches;
    private final TableRepository tables;
    private final PartyRepository parties;
    private final PartyService partyService;
    private final SeatingRepository seatings;
    private final Clock clock;

    public SeatingService(BranchRepository branches, TableRepository tables, PartyRepository parties,
                          PartyService partyService, SeatingRepository seatings, Clock clock) {
                                    this.branches = branches;
        this.tables = tables;
        this.parties = parties;
        this.partyService = partyService;
        this.seatings = seatings;
                this.clock = clock;
    }
    
 /**
 * The checks run in the ticket's order and the first failure wins: party exists, table exists, party not already
 * seated, table cleared, table fits. All the writes are one transaction.
*/
@Transactional
    public SeatingResponse seat(String branch, String partyId, String tableNo) {
long branchId = branches.idOf(branch).orElseThrow();
PartyRepository.Row party = PartyIds.parse(partyId).flatMap(id -> parties.find(branchId, id))
                .orElseThrow(() -> new NoSuchParty(partyId));
                      TableRepository.Row table = tables.findByNo(branchId, tableNo).orElseThrow(() -> new NoSuchTable(branch, tableNo));
if (!"waiting".equals(party.state())) {                   // seated, or already left
    throw new AlreadySeated(partyId);
}
               if (table.seatingId() != null) {
    throw new TableNotCleared(tableNo);
}
                if (table.seats() < party.covers()) {
            throw new TableTooSmall(tableNo, table.seats(), party.covers());
        }
                Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
long seating = seatings.insert(branchId, party.id(), table.id(), now);
tables.seat(table.id(), seating);
        parties.markSeated(party.id());
            int waited = partyService.stampWait(party.id(), party.joinedAt(), now);
 return new SeatingResponse(SeatingIds.format(seating), partyId, tableNo, now, waited);
    }

    /** The party gets up: the seating is stamped and the party has left, but the table is not free until cleared. */
    @Transactional
    public LeaveResponse leave(String branch, String seatingId) {
            SeatingRepository.Row seating = seating(branch, seatingId);
        if (seating.leftAt() != null) {
            throw new AlreadyLeft(seatingId);
        }
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        seatings.leave(seating.id(), now);
        parties.markLeft(seating.partyId());
        return new LeaveResponse(seatingId, now);
    }

        /**
     * The server wipes the table: the seating, the table and the party change together or not at all. The turn is
     * seated to left - what the table earned - not seated to cleared.
     */
         @Transactional
    public ClearResponse clear(String branch, String seatingId) {
        SeatingRepository.Row seating = seating(branch, seatingId);
                if (seating.clearedAt() != null) {
            throw new AlreadyCleared(seatingId);
        }
                if (seating.leftAt() == null) {
            throw new NotLeftYet(seatingId);
        }
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
                int turn = (int) Duration.between(seating.seatedAt(), seating.leftAt()).toMinutes();
        seatings.clear(seating.id(), now, turn);
        tables.clear(seating.tableId(), now);
        parties.markCleared(seating.partyId());
        return new ClearResponse(seatingId, seating.table(), turn, now);
    }
        private SeatingRepository.Row seating(String branch, String seatingId) {
        long branchId = branches.idOf(branch).orElseThrow();
        return SeatingIds.parse(seatingId).flatMap(id -> seatings.find(branchId, id))
                .orElseThrow(() -> new NoSuchSeating(seatingId));
    }
}