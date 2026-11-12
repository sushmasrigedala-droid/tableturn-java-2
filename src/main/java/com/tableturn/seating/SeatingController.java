package com.tableturn.seating;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

/** Seating. There is one branch, Baner. Parses the request and delegates; no rules live here. */
@RestController
public class SeatingController { 

    static final String BRANCH = "Baner";

    private final SeatingService seatings;

    public SeatingController(SeatingService seatings) {
    this.seatings = seatings;
    }

    @PostMapping("/seatings")
    @ResponseStatus(HttpStatus.CREATED)
    public SeatingResponse seat(@Valid @RequestBody SeatRequest body) {
    return seatings.seat(BRANCH, body.partyId(), body.table());
    }
    }