package com.tableturn.table;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.tableturn.error.ProblemExamples;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;

/** The floor. Parses the request and delegates; no rules live here. */
@RestController
@Tag(name = "tables", description = "The floor, and one table")
@RequestMapping("/branches/{branch}/tables")
public class TableController {
    private final TableService tables;

    public TableController(TableService tables) {
        this.tables = tables;
    }

@GetMapping

        @Operation(summary = "The floor: every table with its state, optionally only one section and/or one state")
    @ApiResponse(responseCode = "200", description = "The tables shown and how many of them are free")
    @ApiResponse(responseCode = "400", description = "A section or state that is not allowed", 
    content = @Content(mediaType = ProblemExamples.PROBLEM,
        examples = @ExampleObject(name = "validation", value = ProblemExamples.VALIDATION_QUERY)))
    @ApiResponse(responseCode = "404",description = "No such branch",
    content = @Content(mediaType = ProblemExamples.PROBLEM,
        examples = @ExampleObject(name = "no-such-branch", value = ProblemExamples.NO_SUCH_BRANCH)))
    public FloorView floor(
            @PathVariable String branch,
            @RequestParam(required = false) Section section,
            @RequestParam(required = false) TableState state) {
        return tables.floor(branch, section, state);
            }
    @GetMapping("/{no}")
    @Operation(summary = "One table: its seats, its section, its state and, when seated, whose party and for how long")
    @ApiResponse(responseCode = "200",  description = "The table")
        @ApiResponse(responseCode = "404",description = "No such table, or no such branch",
      content = @Content(mediaType = ProblemExamples.PROBLEM, examples = {
                    @ExampleObject(name = "no-such-table", value = ProblemExamples.NO_SUCH_TABLE),
                    @ExampleObject(name = "no-such-branch", value = ProblemExamples.NO_SUCH_BRANCH)}))
    public TableView table(@PathVariable String branch, @PathVariable String no) {
        return tables.table(branch, no);
    }
}