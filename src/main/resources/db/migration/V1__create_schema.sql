CREATE TABLE app_user
(
    id            UUID         NOT NULL,
    email         VARCHAR(255) NOT NULL,
    password_hash VARCHAR(255) NOT NULL,
    role          VARCHAR(20)  NOT NULL,
    CONSTRAINT pk_app_user PRIMARY KEY (id),
    CONSTRAINT uk_app_user_email UNIQUE (email)
);

CREATE TABLE customer
(
    id                 UUID         NOT NULL,
    first_name         VARCHAR(255) NOT NULL,
    last_name          VARCHAR(255) NOT NULL,
    email              VARCHAR(255) NOT NULL,
    street             VARCHAR(255) NOT NULL,
    house_number       VARCHAR(255) NOT NULL,
    postal_code        VARCHAR(255) NOT NULL,
    city               VARCHAR(255) NOT NULL,
    country            VARCHAR(255) NOT NULL,
    phone_country_code VARCHAR(255) NOT NULL,
    phone_number       VARCHAR(255) NOT NULL,
    CONSTRAINT pk_customer PRIMARY KEY (id),
    CONSTRAINT fk_customer_app_user FOREIGN KEY (id) REFERENCES app_user (id)
);

CREATE TABLE item
(
    id          UUID           NOT NULL,
    name        VARCHAR(255)   NOT NULL,
    description VARCHAR(255)   NOT NULL,
    price       NUMERIC(10, 2) NOT NULL,
    stock       INTEGER        NOT NULL,
    version     BIGINT         NOT NULL,
    CONSTRAINT pk_item PRIMARY KEY (id),
    CONSTRAINT ck_item_price CHECK (price >= 0),
    CONSTRAINT ck_item_stock CHECK (stock >= 0)
);

CREATE TABLE eurder
(
    id          UUID NOT NULL,
    customer_id UUID NOT NULL,
    order_date  DATE NOT NULL,
    CONSTRAINT pk_eurder PRIMARY KEY (id),
    CONSTRAINT fk_eurder_customer FOREIGN KEY (customer_id) REFERENCES customer (id)
);

CREATE TABLE item_group
(
    eurder_id     UUID           NOT NULL,
    line_number   INTEGER        NOT NULL,
    item_id       UUID           NOT NULL,
    item_name     VARCHAR(255)   NOT NULL,
    unit_price    NUMERIC(10, 2) NOT NULL,
    amount        INTEGER        NOT NULL,
    shipping_date DATE           NOT NULL,
    CONSTRAINT pk_item_group PRIMARY KEY (eurder_id, line_number),
    CONSTRAINT fk_item_group_eurder FOREIGN KEY (eurder_id) REFERENCES eurder (id),
    CONSTRAINT fk_item_group_item FOREIGN KEY (item_id) REFERENCES item (id)
);