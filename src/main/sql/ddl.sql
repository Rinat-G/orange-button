create schema orange_button;

create table if not exists ob_user
(
    id                       bigint generated by default as identity (maxvalue 2147483647)
        constraint user_pk
            primary key,
    email                    varchar not null
        constraint user_email_u
            unique,
    pin_hash                 varchar not null,
    default_session_duration integer
);


create table if not exists ob_session
(
    id         uuid    not null,
    user_id    bigint  not null
        constraint session_user_id
            references ob_user
            on delete cascade,
    status     varchar not null,
    start_date date,
    duration   integer
);


create table if not exists ob_guard
(
    id      bigserial not null
        constraint ob_guard_pk
            primary key,
    email   varchar   not null,
    user_id integer   not null
        constraint ob_guard_ob_user_id_fk
            references ob_user
            on delete cascade
);


create unique index if not exists ob_guard_id_uindex
    on ob_guard (id);