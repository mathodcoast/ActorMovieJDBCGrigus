package com.grigus.Utility;

import com.grigus.model.Actor;
import com.grigus.model.Movie;
import org.h2.jdbcx.JdbcDataSource;

import javax.sql.DataSource;

import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.ArrayList;
import java.util.List;

public class ActorMovieUtility {
    private static DataSource dataSource = createInMemoryDefaultH2DataSource();
    private static List<Actor> actorsList = createActorsList();
    private static List<Movie> moviesList = createMoviesList();
    private static Movie movieRedHeat = Movie.builder().name("Red Heat").duration(Duration.ofMinutes(103)).releaseDate(LocalDate.of(1988,Month.JUNE,14)).build();

    private static List<Movie> createMoviesList() {
        List<Movie> moviesList = new ArrayList<>();
        moviesList.add(Movie.builder().name("The Expendables").duration(Duration.ofMinutes(103)).releaseDate(LocalDate.of(2010, Month.AUGUST,3)).build());
        moviesList.add(Movie.builder().name("Armageddon").duration(Duration.ofMinutes(151)).releaseDate(LocalDate.of(1998, Month.JULY,1)).build());
        moviesList.add(Movie.builder().name("The Lord of the Rings: The Return of the King").duration(Duration.ofMinutes(200)).releaseDate(LocalDate.of(2003, Month.DECEMBER,1)).build());
        moviesList.add(Movie.builder().name("Terminator 2: Judgment Day").duration(Duration.ofMinutes(137)).releaseDate(LocalDate.of(1991, Month.JULY,1)).build());
        moviesList.add(Movie.builder().name("Rocky IV").duration(Duration.ofMinutes(90)).releaseDate(LocalDate.of(1985, Month.NOVEMBER,27)).build());
        return moviesList;
    }

    private static List<Actor> createActorsList() {
        List<Actor> actorsList = new ArrayList<>();
        actorsList.add(Actor.builder().firstName("Arnold").lastName("Schwarzenegger").birthday(LocalDate.of(1947,7,30)).build());
        actorsList.add(Actor.builder().firstName("Bruce").lastName("Willis").birthday(LocalDate.of(1946,3,19)).build());
        actorsList.add(Actor.builder().firstName("Sylvester").lastName("Stallone").birthday(LocalDate.of(1947,7,6)).build());
        actorsList.add(Actor.builder().firstName("Dolf").lastName("Lundgren").birthday(LocalDate.of(1957, Month.NOVEMBER,3)).build());
        actorsList.add(Actor.builder().firstName("Ben").lastName("Affleck").birthday(LocalDate.of(1972, Month.AUGUST,15)).build());
        actorsList.add(Actor.builder().firstName("Liv").lastName("Tyler").birthday(LocalDate.of(1977,Month.JULY,1)).build());
        actorsList.add(Actor.builder().firstName("Elijah").lastName("Wood").birthday(LocalDate.of(1981,Month.JANUARY,28)).build());
        return actorsList;
    }



    private static DataSource createInMemoryDefaultH2DataSource() {
        JdbcDataSource h2DataSource = new JdbcDataSource();
        h2DataSource.setUser("grigus");
        h2DataSource.setPassword("griguspass");
        h2DataSource.setUrl("jdbc:h2:mem:actor_movie_db;DB_CLOSE_DELAY=-1;DB_CLOSE_ON_EXIT=false;DATABASE_TO_UPPER=false;");

        System.out.println("DB was created");
        return h2DataSource;
    }

    public static DataSource getDataSource() {
        return dataSource;
    }

    public static List<Actor> getActorsList() {
        return actorsList;
    }

    public static List<Movie> getMoviesList() {
        return moviesList;
    }

    public static Movie getMovieRedHeat() {
        return movieRedHeat;
    }
}
