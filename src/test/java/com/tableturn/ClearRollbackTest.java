package com.tableturn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.doThrow;

import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.tableturn.party.PartyRepository;

/**
 * The clear is one transaction. Moving the party to cleared - the write after the seating and the
 * table - is made to fail; the clear answers 500 and the table is still uncleared, the party still left.
 */
 @SpringBootTest(
        webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
        properties = "spring.datasource.url=jdbc:h2:mem:clear-rollback-test"
)
class ClearRollbackTest {
        @Autowired
    TestRestTemplate http;

    @MockitoSpyBean
    PartyRepository parties;
    @Test
    void aClearThatFailsPartWayChangesNothing() {
        http.postForObject("/waitlist", Map.of("name", "Kulkarni", "covers", 6), Map.class);
 http.postForObject("/seatings",Map.of("partyId", "W001", "table", "T20"), Map.class);
        http.postForObject("/seatings/S001/leave", null, Map.class);
 doThrow(new IllegalStateException("injected: the party cannot be cleared"))
                .when(parties).markCleared(anyLong());

        int status = http.postForEntity("/seatings/S001/clear", null, String.class) .getStatusCode().value();
        assertThat(status).isEqualTo(500);

         assertThat(http.getForObject("/branches/Baner/tables/T20", Map.class).get("state")) .isEqualTo("uncleared");
assertThat(http.getForObject("/parties/W001", Map.class).get("state")).isEqualTo("left");
    }
}
