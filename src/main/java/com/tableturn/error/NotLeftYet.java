package com.tableturn.error;

/** 409 /errors/not-left-yet: clear comes after leave. */
public class NotLeftYet extends DomainException {

    public NotLeftYet(String seatingId) {
        super(409, "/errors/not-left-yet", "Not left yet",
                "The party at seating " + seatingId + " has not left yet: clear comes after leave");
    }
}