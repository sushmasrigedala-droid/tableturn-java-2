package com.tableturn.seating;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.tableturn.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** Seating. There is one branch, Baner. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "seatings", description = "Seat a party; it leaves; the table is cleared")
public class SeatingController { 

    static final String BRANCH = "Baner";

    private final SeatingService seatings;

    public SeatingController(SeatingService seatings) {
    this.seatings = seatings;
    }

    @PostMapping("/seatings")
    @ResponseStatus(HttpStatus.CREATED)
    @Operation(summary = "Seat a party at a table, stamping the real wait")
    @ApiResponse(responseCode = "201", description = "Seated")
    @ApiResponse(responseCode = "400",description = "A body that is not valid",
    content = @Content(mediaType = ProblemExamples.PROBLEM,
        examples = @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_SEAT)))
            @ApiResponse(responseCode = "404",description = "No such party, or no such table",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-party", value = ProblemExamples.NO_SUCH_PARTY_SEAT),
                    @ExampleObject(name = "no-such-table", value = ProblemExamples.NO_SUCH_TABLE_SEAT)}))
                        @ApiResponse(responseCode = "409",description = "Too small, already seated, or not cleared",
            content = @Content(mediaType = ProblemExamples.PROBLEM,examples = {
                    @ExampleObject(name = "table-too-small", value = ProblemExamples.TABLE_TOO_SMALL),
                    @ExampleObject(name = "already-seated", value = ProblemExamples.ALREADY_SEATED),
                    @ExampleObject(name = "table-not-cleared", value = ProblemExamples.TABLE_NOT_CLEARED)}))
    public SeatingResponse seat(@Valid @RequestBody SeatRequest body) {
    return seatings.seat(BRANCH, body.partyId(), body.table());
    }

    @PostMapping("/seatings/{seatingId}/leave")
     @Operation(summary = "The party has left: the table is uncleared until a server clears it")
    @ApiResponse(responseCode = "200", description = "Left")
    @ApiResponse(responseCode = "404",description = "No such seating",
        content = @Content(mediaType = ProblemExamples.PROBLEM,
            examples = @ExampleObject(name = "no-such-seating", value = ProblemExamples.NO_SUCH_SEATING)))
                @ApiResponse(responseCode = "409",description = "The party has already left",
            content = @Content(mediaType = ProblemExamples.PROBLEM,
                examples = @ExampleObject(name = "already-left", value = ProblemExamples.ALREADY_LEFT)))
       public LeaveResponse leave(@PathVariable String seatingId) {
      return seatings.leave(BRANCH, seatingId);
    }

        @PostMapping("/seatings/{seatingId}/clear")
        @Operation(summary = "Clear the table: it is free again, and the turn (seated to left) is stamped")
    @ApiResponse(responseCode = "200", description = "Cleared; the table is free")
    @ApiResponse(responseCode = "404", description = "No such seating",
            content = @Content(mediaType = ProblemExamples.PROBLEM,
                examples = @ExampleObject(name = "no-such-seating", value = ProblemExamples.NO_SUCH_SEATING)))
                    @ApiResponse(responseCode = "409",description = "The party has not left yet, or the table is already cleared",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "not-left-yet", value = ProblemExamples.NOT_LEFT_YET),
                    @ExampleObject(name = "already-cleared", value = ProblemExamples.ALREADY_CLEARED)}))
    public ClearResponse clear(@PathVariable String seatingId) {
        return seatings.clear(BRANCH, seatingId);
    }
    }