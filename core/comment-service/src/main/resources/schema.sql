DROP TABLE IF EXISTS comments;

CREATE TABLE comments
(
    id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
    text         VARCHAR,
    author_id    BIGINT,
    event_id     BIGINT,
    published_on TIMESTAMP WITHOUT TIME ZONE,
    is_updated   BOOLEAN,
    updated_on   TIMESTAMP WITHOUT TIME ZONE,
    FOREIGN KEY (author_id) REFERENCES users (id) ON DELETE CASCADE,
    FOREIGN KEY (event_id) REFERENCES events (id) ON DELETE CASCADE
);