-- The branch and its tables. Run at every start-up, so every table is created only if missing.
-- A table's state is not stored: it is derived from its current seating (none yet: every table is free).

CREATE TABLE IF NOT EXISTS branches (
    id   BIGINT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE,
    name VARCHAR(80) NOT NULL
);

CREATE TABLE IF NOT EXISTS tables (
    id                 BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id          BIGINT      NOT NULL REFERENCES branches (id),
    no                 VARCHAR(8)  NOT NULL,
    seats              INT         NOT NULL,
    section            VARCHAR(10) NOT NULL,
    current_seating_id BIGINT,
    cleared_at         TIMESTAMP WITH TIME ZONE,
    UNIQUE (branch_id, no)
);

-- The waitlist. A party's position is counted when it joins (1 + the parties waiting) and stored.
-- The wait the party was quoted when it joined, and the real wait, stamped once when it is seated.
CREATE TABLE IF NOT EXISTS parties (
        id             BIGINT AUTO_INCREMENT PRIMARY KEY,
        branch_id      BIGINT                   NOT NULL REFERENCES branches (id),
        name           VARCHAR(60)             NOT NULL,
        covers         INT                      NOT NULL,
        phone          VARCHAR(16),
        state          VARCHAR(10)              NOT NULL,
        joined_at      TIMESTAMP WITH TIME ZONE NOT NULL,
        position       INT                      NOT NULL,
        quoted_minutes INT                      NOT NULL,
        quoted_at      TIMESTAMP WITH TIME ZONE NOT NULL,
        waited_minutes INT
);

-- A party at a table. left_at, cleared_at and turn_minutes are filled when the party leaves and the table is
-- cleared; tables.current_seating_id points here while the seating is on the floor.
CREATE TABLE IF NOT EXISTS seatings (
    id           BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id    BIGINT                   NOT NULL REFERENCES branches (id),
    party_id     BIGINT                   NOT NULL REFERENCES parties (id),
    table_id     BIGINT                   NOT NULL REFERENCES tables (id),
    seated_at    TIMESTAMP WITH TIME ZONE NOT NULL,
    left_at      TIMESTAMP WITH TIME ZONE,
    cleared_at   TIMESTAMP WITH TIME ZONE,
    turn_minutes INT
);

-- The append-only log of every transition. The app only inserts and reads it (EventLog has nothing else);
-- data holds the event's facts as JSON - ids and numbers, never a phone number.
CREATE TABLE IF NOT EXISTS events (
    id         BIGINT AUTO_INCREMENT PRIMARY KEY,
    branch_id  BIGINT                   NOT NULL REFERENCES branches (id),
    party_id   BIGINT                   NOT NULL REFERENCES parties (id),
    seating_id BIGINT REFERENCES seatings (id),
    kind       VARCHAR(10)              NOT NULL,
    at         TIMESTAMP WITH TIME ZONE NOT NULL,
    data       VARCHAR(2000)            NOT NULL
);