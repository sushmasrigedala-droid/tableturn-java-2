package com.tableturn;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tableturn.error.TableNotCleared;
import com.tableturn.error.TableTooSmall;
import com.tableturn.seating.SeatingController;
import com.tableturn.seating.SeatingService;

/**
* Only the web layer - the seating controller and the one exception handler - with the service replaced by a
* mock. It proves each refusal the service throws is mapped to its status and type, without a database.
    */
    @WebMvcTest(SeatingController.class)
class SeatingControllerSliceTest {

    @Autowired
    MockMvc http;

    @MockitoBean
    SeatingService seatings;

    @Test
    void tableTooSmallIsMappedTo409() throws Exception {
        doThrow(new TableTooSmall("T11", 4, 6)).when(seatings).seat("Baner", "W031", "T11");
        http.perform(post("/seatings") .contentType(MediaType.APPLICATION_JSON)
.content("{\"partyId\":\"W031\",\"table\":\"T11\"}"))
.andExpect(status().isConflict())
.andExpect(jsonPath("$.type").value("/errors/table-too-small"));
    }

        @Test
    void tableNotClearedIsMappedTo409WithItsOwnType() throws Exception {
        doThrow(new TableNotCleared("T12")).when(seatings).seat("Baner", "W031", "T12");
         http.perform(post("/seatings") .contentType(MediaType.APPLICATION_JSON)
.content("{\"partyId\":\"W031\",\"table\":\"T12\"}"))
            .andExpect(status().isConflict())
            .andExpect(jsonPath("$.type").value("/errors/table-not-cleared"));
    }
}