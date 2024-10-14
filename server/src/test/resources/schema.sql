CREATE TABLE IF NOT EXISTS users
(
  id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name     VARCHAR(255) NOT NULL,
  email    VARCHAR(255) NOT NULL UNIQUE
);

CREATE TABLE IF NOT EXISTS requests
(
  id           BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  requester_id BIGINT                      NOT NULL,
  description  VARCHAR(255)                NOT NULL,
  create_date  TIMESTAMP WITHOUT TIME ZONE NOT NULL,
  CONSTRAINT fk_requests_requester_id FOREIGN KEY (requester_id) REFERENCES users (id)
);

CREATE TABLE IF NOT EXISTS items
(
  id      BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  name         VARCHAR(255) NOT NULL,
  description  VARCHAR(255) NOT NULL,
  available BOOLEAN NOT NULL DEFAULT FALSE,
  owner_id     BIGINT  NOT NULL REFERENCES users (id),
  request_id   BIGINT           REFERENCES requests (id)
);

CREATE TABLE IF NOT EXISTS bookings
(
  booking_id  BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  start_date  TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
  finish_date TIMESTAMP WITHOUT TIME ZONE     NOT NULL,
  item_id     BIGINT                          NOT NULL REFERENCES items (id),
  booker_id   BIGINT                          NOT NULL REFERENCES users (id),
  status      VARCHAR(50)                     NOT NULL,
  CONSTRAINT chk_start_end_order CHECK (start_date < finish_date),
  CONSTRAINT status_values CHECK (status IN ('WAITING', 'APPROVED', 'REJECTED'))
);

CREATE TABLE IF NOT EXISTS comments
(
  comment_id BIGINT GENERATED ALWAYS AS IDENTITY PRIMARY KEY,
  text       VARCHAR(255)                NOT NULL,
  item_id    BIGINT                      NOT NULL REFERENCES items (id),
  author_id  BIGINT                      NOT NULL REFERENCES users (id),
  created    TIMESTAMP WITHOUT TIME ZONE NOT NULL
);

CREATE OR REPLACE VIEW last_bookings
AS
SELECT booking_id,
       item_id
FROM (SELECT booking_id,
             item_id,
             ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY start_date DESC) AS r
      FROM bookings
      WHERE status = 'APPROVED'
        AND start_date <= CURRENT_TIMESTAMP) AS t
WHERE r = 1;

CREATE OR REPLACE VIEW next_bookings
AS
SELECT booking_id,
       item_id
FROM (SELECT booking_id,
             item_id,
             ROW_NUMBER() OVER (PARTITION BY item_id ORDER BY start_date) AS r
      FROM bookings
      WHERE status = 'APPROVED'
        AND start_date > CURRENT_TIMESTAMP) AS t
WHERE r = 1;

