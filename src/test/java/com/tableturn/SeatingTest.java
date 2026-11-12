package com.tableturn;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

/** Seating a party, and each of the three ways the floor says no, on the whole app.*/
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:seating-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class SeatingTest {

    @Autowired
    MockMvc http;

    void join(String name, int covers) throws Exception {
        http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON)
                .content("{\"name\":\"" + name + "\",\"covers\":" + covers + "}")).andExpect(status().isCreated());
    }

    ResultActions seat(String party, String table) throws Exception {
        return http.perform(post("/seatings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"partyId\":\"" + party + "\",\"table\":\"" + table + "\"}"));
    }

        @Test
    void theKulkarnisSitAtASixTopAndTheTableShowsThem() throws Exception {
        join("Kulkarni", 6);
        seat("W001", "T20").andExpect(status().isCreated())
.andExpect(jsonPath("$.table").value("T20"))
.andExpect(jsonPath("$.partyId").value("W001"));
http.perform(get("/branches/Baner/tables/T20"))
.andExpect(jsonPath("$.state").value("seated"))
 .andExpect(jsonPath("$.partyId").value("W001"));
http.perform(get("/parties/W001")).andExpect(jsonPath("$.table").value("T20"));
    }

        @Test
    void sixAtAFourTopIsTableTooSmall() throws Exception {
        join("Kulkarni", 6);
        seat("W001", "T11") .andExpect(status().isConflict())
.andExpect(jsonPath("$.type").value("/errors/table-too-small"));
    }

        @Test
    void seatingThemAgainIsAlreadySeated() throws Exception {
        join("Kulkarni", 6);
seat("W001", "T20").andExpect(status().isCreated());
seat("W001", "T21") .andExpect(status().isConflict())
.andExpect(jsonPath("$.type").value("/errors/already-seated"));
    }

    @Test
    void aTableStillInUseIsNotCleared() throws Exception {
        join("A", 2);
        join("B", 2);
        seat("W001", "T01").andExpect(status().isCreated());
        seat("W002", "T01").andExpect(status().isConflict())
.andExpect(jsonPath("$.type").value("/errors/table-not-cleared"));
    }
    }
