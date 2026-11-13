package com.tableturn.party;

import java.time.Duration;
import java.time.Instant;

import org.springframework.stereotype.Service;

import com.tableturn.error.NoSuchParty;
import com.tableturn.table.BranchRepository;

/**  One party: read back with its quote, its real wait and its timeline; and the real wait, stamped at seating. */
@Service
public class PartyService {

private final BranchRepository	branches;
  private final PartyRepository	parties;
  private final EventLog events;

public PartyService(BranchRepository branches, PartyRepository parties, EventLog events) {
        this.branches = branches;
        this.parties = parties;
         this.events = events;
    }

   public PartyView party(String branch, String partyId) {
        long branchId = branches.idOf(branch).orElseThrow();      // the one branch is seeded at start-up
        PartyRepository.Row row = PartyIds.parse(partyId).flatMap(id -> parties.find(branchId, id))
                .orElseThrow(() -> new NoSuchParty(partyId));
                                return new PartyView(partyId, row.name(), row.covers(), row.state(), row.position(), row.joinedAt(),
                row.quotedMinutes(), row.quotedAt(), row.waitedMinutes(), row.table(), events.forParty(row.id()));
    }

            /**
     * The real wait, stamped once at seating (the seating calls this with its moment): whole minutes since the party
     * joined, rounded down, and stored - a read never recomputes it.
     */
         public int stampWait(long partyId, Instant joinedAt, Instant seatedAt) {
        int waited = (int) Duration.between(joinedAt, seatedAt).toMinutes();
        parties.stampWait(partyId, waited);
        return waited;
    }
}