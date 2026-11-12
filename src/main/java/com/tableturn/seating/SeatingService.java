package com.tableturn.seating;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tableturn.error.AlreadySeated;
import com.tableturn.error.NoSuchParty;
import com.tableturn.error.NoSuchTable;
import com.tableturn.error.TableNotCleared;
import com.tableturn.error.TableTooSmall;
import com.tableturn.party.PartyIds;
import com.tableturn.party.PartyRepository;
import com.tableturn.party.PartyService;
import com.tableturn.table.BranchRepository;
import com.tableturn.table.TableRepository;

/** Seating a party at a table. Every refusal is thrown from here and mapped once, by the exception handler.*/
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
if ("seated".equals(party.state())) {
            throw new AlreadySeated(partyId);
        }
                if (table.currentSeatingId() != null) {
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
}