package com.tableturn;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyMap;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.ArgumentMatchers.isNull;
import static org.mockito.Mockito.doThrow;

import java.time.Instant;
import java.util.Map;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.test.context.bean.override.mockito.MockitoSpyBean;

import com.tableturn.party.EventLog;

/**
 * An event is written in the same transaction as the change it records. The join's `quoted` event is
 * made to fail; the join answers 500 and leaves no party behind.
 */
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT,
    properties = "spring.datasource.url=jdbc:h2:mem:join-rollback-test")
class JoinRollbackTest {

        @Autowired
    TestRestTemplate http;

    @MockitoSpyBean
    EventLog events;

@Test
 void aJoinWhoseEventFailsLeavesNoParty() {
        doThrow(new IllegalStateException("injected: the quoted event cannot be written"))
 .when(events).append(anyLong(), anyLong(), isNull(), eq("quoted"), any(Instant.class), anyMap());

                        int status = http.postForEntity("/waitlist", Map.of("name", "Kulkarni", "covers", 6), String.class)
                .getStatusCode().value();
        assertThat(status).isEqualTo(500);

                assertThat(http.getForEntity("/parties/W001", String.class).getStatusCode().value()).isEqualTo(404);
    }
}

