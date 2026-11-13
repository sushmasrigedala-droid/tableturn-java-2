package com.tableturn;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;
import org.springframework.test.web.servlet.MockMvc;

import com.tableturn.party.PartyService;

/** The API docs the kitchen-screen developer builds against, and the one shape every error has - a 500 too. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:api-docs-test")
@AutoConfigureMockMvc
class ApiDocsTest {

    @Autowired
    MockMvc http;

    @MockitoSpyBean
    PartyService parties;

    @Test
    void theSpecDocumentsEveryEndpointAndItsRefusals() throws Exception {
        http.perform(get("/v3/api-docs"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.paths['/seatings'].post.summary").isNotEmpty())
                                .andExpect(jsonPath("$.paths['/seatings'].post.responses['409']").exists())
                .andExpect(jsonPath("$.paths['/waitlist'].post.responses['400']").exists())
                .andExpect(jsonPath("$.paths['/parties/{partyId}'].get.tags[0]").value("parties"))
                .andExpect(jsonPath("$.paths['/seatings/{seatingId}/clear'].post.responses['409']").exists());
                 }

                     @Test
    void anUnexpectedErrorIsAGeneric500WithARequestId() throws Exception {
        doThrow(new IllegalStateException("the kitchen is on fire")).when(parties).party(anyString(), anyString());
        http.perform(get("/parties/W001"))
         .andExpect(status().isInternalServerError())
                .andExpect(jsonPath("$.type").value("/errors/internal"))
                .andExpect(jsonPath("$.requestId").isNotEmpty())
                .andExpect(content().string(Matchers.not(Matchers.containsString("on fire"))));
    }
}