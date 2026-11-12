package com.tableturn.error;

/** 409 /errors/already-cleared. */
public class AlreadyCleared extends DomainException {

    public AlreadyCleared(String seatingId) {
        super(409, "/errors/already-cleared", "Already cleared", "Seating " + seatingId + " has already been cleared");
    }
}