drop table IF EXISTS FILM_GENRE;

drop table IF EXISTS FRIENDS_STATUS;

drop table IF EXISTS GENRE;

drop table IF EXISTS LIKES;

drop table IF EXISTS FILM;

drop table IF EXISTS MPA_TYPE;

drop table IF EXISTS `USER`;

CREATE TABLE IF NOT EXISTS `user`(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    birthday DATE,
    login VARCHAR,
    name VARCHAR,
    email VARCHAR
    CONSTRAINT birthday_not_in_future CHECK (birthday < CAST(NOW() AS DATE))
);

CREATE TABLE IF NOT EXISTS mpa_type(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR
);

CREATE TABLE IF NOT EXISTS film(
    id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
    name VARCHAR NOT NULL,
    description VARCHAR(200),
    releaseDate DATE,
    duration INTEGER,
    mpaId BIGINT REFERENCES mpa_type (id),
    CONSTRAINT positive_number CHECK (duration > 0)
);

CREATE TABLE IF NOT EXISTS genre(
   id BIGINT GENERATED BY DEFAULT AS IDENTITY PRIMARY KEY,
   name VARCHAR
);

CREATE TABLE IF NOT EXISTS friends_status(
    friend_one_id BIGINT REFERENCES `user` (id),
    friend_two_id BIGINT REFERENCES `user` (id),
    status INTEGER NOT NULL,
    CONSTRAINT unique_friend_pair UNIQUE(friend_one_id, friend_two_id)

);

CREATE TABLE IF NOT EXISTS likes(
    filmId BIGINT REFERENCES film (id),
    userId BIGINT NULL
    CONSTRAINT userId REFERENCES `user` (id)
);

CREATE TABLE IF NOT EXISTS film_genre(
    filmId BIGINT REFERENCES film (id),
    genreId INTEGER REFERENCES genre (id),
    CONSTRAINT unique_genre_pair UNIQUE (filmId, genreId)
);

---> add genres
INSERT INTO GENRE (NAME) VALUES ('Комедия');
INSERT INTO GENRE (NAME) VALUES ('Драма');
INSERT INTO GENRE (NAME) VALUES ('Мультфильм');
INSERT INTO GENRE (NAME) VALUES ('Триллер');
INSERT INTO GENRE (NAME) VALUES ('Документальный');
INSERT INTO GENRE (NAME) VALUES ('Боевик');

---> add mpa
INSERT INTO MPA_TYPE (NAME) VALUES ( 'G' );
INSERT INTO MPA_TYPE (NAME) VALUES ( 'PG' );
INSERT INTO MPA_TYPE (NAME) VALUES ( 'PG-13' );
INSERT INTO MPA_TYPE (NAME) VALUES ( 'R' );
INSERT INTO MPA_TYPE (NAME) VALUES ( 'NC-17' );


