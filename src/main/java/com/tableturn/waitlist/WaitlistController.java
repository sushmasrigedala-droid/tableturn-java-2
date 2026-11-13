package com.tableturn.waitlist;

import java.net.URI;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.tableturn.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;

/** The waitlist. There is one branch, Baner. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "waitlist", description = "A party joins the list and is quoted a wait")
public class WaitlistController {

    static final String BRANCH = "Baner";

    private final WaitlistService waitlist;

    public WaitlistController(WaitlistService waitlist) {
        this.waitlist = waitlist;
    }

    @PostMapping("/waitlist")
    @Operation(summary = "Add a party to the list: its id, its place in the queue and the wait it is quoted")
    @ApiResponse(responseCode = "201", description = "On the list; the party is at the Location given")
    @ApiResponse(responseCode = "400", description = "A body that is not valid, or a party no table here could seat",
            content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_JOIN),
                    @ExampleObject(name = "party-too-big", value = ProblemExamples.PARTY_TOO_BIG)}))
    public ResponseEntity<JoinResponse> join(@Valid @RequestBody JoinRequest body) {
        JoinResponse joined = waitlist.join(BRANCH, body.name(), body.covers(), body.phone());
        return ResponseEntity.created(URI.create("/parties/" + joined.partyId())).body(joined);
    }
}