package com.tableturn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;

import com.tableturn.party.EventLog;

/** Every transition is logged in order, and the log can only be added to and read. */
@SpringBootTest(properties = "spring.datasource.url=jdbc:h2:mem:event-log-test")
@AutoConfigureMockMvc
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class EventLogTest {

@Autowired
MockMvc http;

    @Test
    void theKulkarnisTimelineIsJoinedQuotedSeatedLeftCleared() throws Exception {
        http.perform(post("/waitlist").contentType(MediaType.APPLICATION_JSON)
                        .content("{\"name\":\"Kulkarni\",\"covers\":6,\"phone\":\"+919876543210\"}"))
                .andExpect(status().isCreated());
                  http.perform(post("/seatings").contentType(MediaType.APPLICATION_JSON)
                .content("{\"partyId\":\"W001\",\"table\":\"T20\"}")).andExpect(status().isCreated());
http.perform(post("/seatings/S001/leave")).andExpect(status().isOk());
http.perform(post("/seatings/S001/clear")).andExpect(status().isOk());   

        String events = http.perform(get("/parties/W001"))
                .andExpect(jsonPath("$.events.length()").value(5))
                .andExpect(jsonPath("$.events[0].kind").value("joined"))
                .andExpect(jsonPath("$.events[2].kind").value("seated"))
                .andExpect(jsonPath("$.events[2].table").value("T20"))
                            .andExpect(jsonPath("$.events[4].kind").value("cleared"))
                .andReturn().getResponse().getContentAsString();
        assertThat(events).doesNotContain("9876543210");
    }    

        @Test
    void theLogCanOnlyAppendAndRead() {
        String[] publicMethods = Arrays.stream(EventLog.class.getDeclaredMethods())
                .filter(m -> Modifier.isPublic(m.getModifiers()) && !m.isSynthetic())
                .map(Method::getName).sorted().toArray(String[]::new);
              assertThat(publicMethods).containsExactly("append", "forParty");
    }
}          
