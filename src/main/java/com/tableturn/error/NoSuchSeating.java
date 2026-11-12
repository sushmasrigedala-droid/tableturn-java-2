package com.tableturn.error;

/** 404 /errors/no-such-seating. */
public class NoSuchSeating extends DomainException {

    public NoSuchSeating(String seatingId) {
        super(404, "/errors/no-such-seating", "No such seating", "There is no seating " + seatingId);
    }
}