CREATE TABLE actors (
id SERIAL NOT NULL,
first_name VARCHAR(255) NOT NULL,
last_name VARCHAR(255) NOT NULL,
birthday DATE NOT NULL,
CONSTRAINT actor_pk PRIMARY KEY (id)
);

CREATE TABLE movies (
id SERIAL NOT NULL,
 name VARCHAR(255) NOT NULL,
 duration BIGINT NOT NULL,
 release_date DATE NOT NULL,
CONSTRAINT movies_pk PRIMARY KEY (id),
CONSTRAINT movies_uq UNIQUE (name)
);

CREATE TABLE actors_movies (
actor_id BIGINT NOT NULL,
movie_id BIGINT NOT NULL,
CONSTRAINT actors_movies_pk PRIMARY KEY (actor_id, movie_id),
CONSTRAINT actors_movies_actors_fk FOREIGN KEY (actor_id) references actors,
CONSTRAINT actors_movies_movies_fk FOREIGN KEY (movie_id) references movies
);

