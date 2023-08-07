CREATE TABLE IF NOT EXISTS payment
(
    id                 UUID NOT NULL,
    created_by         VARCHAR(100),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(100),
    last_modified_date TIMESTAMP,
    version            INTEGER,
    PRIMARY KEY (id)
);

CREATE TABLE IF NOT EXISTS product
(
    id                 UUID NOT NULL,
    created_by         VARCHAR(100),
    created_date       TIMESTAMP,
    last_modified_by   VARCHAR(100),
    last_modified_date TIMESTAMP,
    version            INTEGER,
    PRIMARY KEY (id)
);