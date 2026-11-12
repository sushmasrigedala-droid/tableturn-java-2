package com.tableturn.error;

/** 409 /errors/already-left. */
public class AlreadyLeft extends DomainException {

    public AlreadyLeft(String seatingId) {
        super(409, "/errors/already-left", "Already left", "The party at seating " + seatingId + " has already left");
    }
}