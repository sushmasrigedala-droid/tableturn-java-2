package com.tableturn.error;

/** 409 /errors/already-seated: the party is no longer waiting - it is at a table, or has already left. */
public class AlreadySeated extends DomainException {

        public AlreadySeated(String partyId) {
        super(409, "/errors/already-seated", "Already seated", "Party " + partyId + " is already at a table");
    }
}
