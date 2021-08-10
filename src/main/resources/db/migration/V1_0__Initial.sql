CREATE TABLE IF NOT EXISTS "best_account" (
                                              id        bigint generated by default as identity,
                                              available decimal(36, 18) not null,
                                              changed   timestamp,
                                              comment   varchar(200),
                                              created   timestamp,
                                              credit    decimal(36, 18) not null,
                                              status    varchar(255)    not null,
                                              "user"    bigint          not null,
                                              primary key (id)
);

CREATE TABLE IF NOT EXISTS "best_authorities"
(
    user_id   bigint       not null,
    authority varchar(255) not null,
    primary key (user_id, authority)
);


CREATE TABLE IF NOT EXISTS "best_cart"
(
    id      bigint generated by default as identity,
    changed timestamp,
    created timestamp,
    status  varchar(255),
    user_id bigint,
    primary key (id)
);


CREATE TABLE IF NOT EXISTS "best_cart_detail"
(
    id       bigint generated by default as identity,
    price    decimal(19, 2),
    product  bigint,
    quantity integer,
    total    decimal(19, 2),
    type     integer,
    primary key (id)
);


CREATE TABLE IF NOT EXISTS "best_cart_detail_entities"
(
    "cart_entity_id"     bigint not null,
    "detail_entities_id" bigint not null
);


CREATE TABLE IF NOT EXISTS "best_order"
(
    id       bigint generated by default as identity,
    cart_id  bigint,
    changed  timestamp,
    created  timestamp,
    discount decimal(19, 2),
    price    decimal(19, 2),
    status   varchar(255),
    total    decimal(19, 2),
    user_id  bigint,
    primary key (id)
);


CREATE TABLE IF NOT EXISTS "best_product"
(
    id          bigint generated by default as identity,
    changed     timestamp,
    created     timestamp,
    description varchar(255),
    name        varchar(255),
    price       decimal(19, 2),
    status      varchar(255),
    type        varchar(255),
    primary key (id)
);

CREATE TABLE IF NOT EXISTS "best_transaction"
(
    id            bigint generated by default as identity,
    amount        decimal(36, 18) not null,
    changed       timestamp,
    created       timestamp,
    order_id      bigint          not null,
    reject_reason varchar(200),
    status        varchar(255)    not null,
    user_id       bigint          not null,
    primary key (id)
);

CREATE TABLE IF NOT EXISTS "best_users"
(
    id       bigint generated by default as identity,
    changed  timestamp,
    created  timestamp,
    email    varchar(254),
    family   varchar(50),
    name     varchar(50),
    status   varchar(255),
    username varchar(50) not null,
    primary key (id)
);