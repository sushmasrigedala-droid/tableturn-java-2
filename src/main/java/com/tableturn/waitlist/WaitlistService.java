package com.tableturn.waitlist;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tableturn.config.TableTurnSettings;
import com.tableturn.error.PartyTooBig;
import com.tableturn.party.EventLog;
import com.tableturn.party.PartyIds;
import com.tableturn.party.PartyRepository;
import com.tableturn.table.BranchRepository;
import com.tableturn.table.TableRepository;

/** The waitlist: parties join it, quoted a wait. Every time comes from the injected clock. */
@Service
public class WaitlistService {

    private final BranchRepository branches;
    private final TableRepository tables;
    private final PartyRepository parties;
    private final Clock clock;
    private final TableTurnSettings settings;
    private final EventLog events;

    public WaitlistService (BranchRepository branches, TableRepository tables, PartyRepository parties,Clock clock, 
    TableTurnSettings settings, EventLog events) {
        this.branches = branches;
        this.tables = tables;
        this.parties = parties;
        this.clock = clock;
        this.settings = settings;
        this.events = events;
    }

        /**
     * A party no table in the branch could ever seat is refused before anything is written. The quote is
     * the default-quote-minutes setting, stored with the moment the party joined; the join and its two events are one
     * transaction.
     */
    @Transactional
    public JoinResponse join(String branch, String name, int covers, String phone) {
        long branchId = branches.idOf(branch).orElseThrow();      // the one branch is seeded at start-up
        int maxCovers = tables.largest(branchId);
        if (covers > maxCovers) {
            throw new PartyTooBig(covers, maxCovers);
        }
        Instant now = clock.instant().truncatedTo(ChronoUnit.SECONDS);
        long id = parties.insert(branchId, name, covers, phone, now, settings.defaultQuoteMinutes());
        events.append(branchId, id, null, "joined", now, Map.of("covers", covers));
        events.append(branchId, id, null, "quoted", now,Map.of("quotedMinutes", settings.defaultQuoteMinutes()));
        PartyRepository.Row row = parties.find(branchId, id).orElseThrow();
        return new JoinResponse(PartyIds.format(id), row.name(), row.covers(), row.position(), row.joinedAt(),
                row.quotedMinutes(), row.quotedAt());
    }
}
