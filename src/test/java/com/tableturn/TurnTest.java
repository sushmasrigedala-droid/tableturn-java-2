package com.tableturn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** The Kulkarnis leave and Joseph clears their table - each step once, and in that order. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:turn-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class TurnTest {

        @Autowired
    MockMvc http;

        @BeforeEach
    void theKulkarnisAreAtT20() throws Exception {
        http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"Kulkarni\",\"covers\":6}")).andExpect(status().isCreated());
http.perform(post("/seatings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"partyId\":\"W001\",\"table\":\"T20\"}")) .andExpect(status().isCreated());
    }

  ResultActions leave() throws Exception {
        return http.perform(post("/seatings/S001/leave"));
    }

        ResultActions clear() throws Exception {
            return http.perform(post("/seatings/S001/clear"));
    }

        @Test
    void leavingMakesTheTableUnclearedAndClearingFreesIt() throws Exception {
        leave().andExpect(status().isOk()).andExpect(jsonPath("$.seatingId").value("S001"));
                http.perform(get("/branches/Baner/tables/T20")).andExpect(jsonPath("$.state").value("uncleared"));
clear().andExpect(status().isOk())
                .andExpect(jsonPath("$.table").value("T20"))
                .andExpect(jsonPath("$.turnMinutes").value(0));
        http.perform(get("/branches/Baner/tables/T20")).andExpect(jsonPath("$.state").value("free"));
http.perform(get("/parties/W001")) .andExpect(jsonPath("$.state").value("cleared"));
    }
       
        @Test
    void leavingTwiceIsAlreadyLeft() throws Exception {
        leave().andExpect(status().isOk());
        leave().andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("/errors/already-left"));
    }

        @Test
    void clearingTwiceIsAlreadyCleared() throws Exception {
        leave().andExpect(status().isOk());
        clear().andExpect(status().isOk());
                clear().andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("/errors/already-cleared"));
    }

        @Test
    void clearingBeforeTheyLeaveIsNotLeftYet() throws Exception {
        clear().andExpect(status().isConflict()).andExpect(jsonPath("$.type").value("/errors/not-left-yet"));
    }
}
    