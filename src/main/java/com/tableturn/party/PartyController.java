package com.tableturn.party;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.tableturn.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** One party. There is one branch, Baner. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "parties", description = "One party and its timeline")
public class PartyController{

    static final String BRANCH = "Baner";

    private final PartyService parties;

    public PartyController(PartyService parties) {
      this.parties = parties;
    }

    @GetMapping("/parties/{partyId}")
        @Operation(summary = "One party: its quote, its real wait, its table and every event of its evening in order")
    @ApiResponse(responseCode = "200", description = "The party")
    @ApiResponse( responseCode = "404", description = "No such party on this list",
            content = @Content(mediaType = ProblemExamples.PROBLEM,
                    examples = @ExampleObject(name = "no-such-party", value = ProblemExamples.NO_SUCH_PARTY)))
    public PartyView party(@PathVariable String partyId) {
        return parties.party(BRANCH, partyId);
    }
}