DROP TABLE IF EXISTS requests;

CREATE TABLE IF NOT EXISTS requests
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    created      TIMESTAMP NOT NULL,
    event_id     BIGINT    NOT NULL,
    requester_id BIGINT    NOT NULL,
    status       VARCHAR   NOT NULL,

    CONSTRAINT fk_event FOREIGN KEY (event_id)
        REFERENCES events (id) ON DELETE CASCADE,
    CONSTRAINT fk_requester FOREIGN KEY (requester_id)
        REFERENCES users (id) ON DELETE CASCADE
);
