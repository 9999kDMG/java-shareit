CREATE TYPE IF NOT EXISTS BOOKING_STATUS AS ENUM
    ('WAITING', 'APPROVED', 'REJECTED', 'CANCELED');

CREATE TABLE IF NOT EXISTS USERS
(
    USER_ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    NAME    VARCHAR(20)        NOT NULL,
    EMAIL   VARCHAR(50) UNIQUE NOT NULL
);

CREATE TABLE IF NOT EXISTS ITEMS
(
    ITEM_ID     INTEGER AUTO_INCREMENT PRIMARY KEY,
    NAME        VARCHAR(100) NOT NULL,
    DESCRIPTION VARCHAR(200),
    AVAILABLE   BOOLEAN      NOT NULL,
    OWNER_ID    INTEGER      NOT NULL,
    FOREIGN KEY (OWNER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS BOOKINGS
(
    BOOKING_ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    START_TIME TIMESTAMP      NOT NULL,
    END_TIME   TIMESTAMP      NOT NULL,
    ITEM_ID    INTEGER        NOT NULL,
    BOOKER_ID  INTEGER        NOT NULL,
    STATUS     BOOKING_STATUS NOT NULL,
    FOREIGN KEY (ITEM_ID) REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE,
    FOREIGN KEY (BOOKER_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);

CREATE TABLE IF NOT EXISTS COMMENTS
(
    COMMENT_ID INTEGER AUTO_INCREMENT PRIMARY KEY,
    TEXT       VARCHAR(500) NOT NULL,
    ITEM_ID    INTEGER      NOT NULL,
    AUTHOR_ID  INTEGER      NOT NULL,
    CREATED    TIMESTAMP    NOT NULL,
    FOREIGN KEY (ITEM_ID) REFERENCES ITEMS (ITEM_ID) ON DELETE CASCADE,
    FOREIGN KEY (AUTHOR_ID) REFERENCES USERS (USER_ID) ON DELETE CASCADE
);