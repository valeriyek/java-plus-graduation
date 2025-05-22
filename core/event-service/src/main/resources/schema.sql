DROP TABLE IF EXISTS events;


CREATE TABLE IF NOT EXISTS events
 (
     id                 BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
     annotation         VARCHAR NOT NULL,
     description        VARCHAR NOT NULL,
     event_date         TIMESTAMP,
     lat                DOUBLE PRECISION,
     lon                DOUBLE PRECISION,
     paid               BOOLEAN       NOT NULL,
     participant_limit  INTEGER       NOT NULL,
     request_moderation BOOLEAN       NOT NULL,
     state              VARCHAR       NOT NULL,
     title              VARCHAR       NOT NULL,
     created_on         TIMESTAMP     NOT NULL,
     published_on       TIMESTAMP,
     initiator_id       BIGINT        NOT NULL,
     category_id        BIGINT,
     views              BIGINT       NOT NULL,
     confirmed_requests BIGINT        NOT NULL,

);

