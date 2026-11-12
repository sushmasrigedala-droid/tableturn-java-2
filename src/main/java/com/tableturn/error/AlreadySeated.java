package com.tableturn.error;

/** 409 /errors/already-seated: the party is already at a table. */
public class AlreadySeated extends DomainException {

        public AlreadySeated(String partyId) {
        super(409, "/errors/already-seated", "Already seated", "Party " + partyId + " is already at a table");
    }
}
