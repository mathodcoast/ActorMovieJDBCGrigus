package com.grigus;

import com.grigus.Utility.ActorMovieUtility;
import com.grigus.dao.*;
import com.grigus.model.Actor;
import com.grigus.model.Movie;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import javax.sql.DataSource;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;


@RunWith(JUnit4.class)
public class ActorMovieTest {
    private static DataSource h2dataSource;
    private static ActorDao actorDao;
    private static MovieDao movieDao;

    @BeforeClass
    public static void init() throws SQLException, IOException, URISyntaxException {
        h2dataSource = ActorMovieUtility.getDataSource();
        createActorMovieTable(h2dataSource);
        actorDao = new ActorDaoImpl();
        movieDao = new MovieDaoImpl();
    }

    @Test
    public void testCreateTables() throws SQLException {
        try (Connection connection = h2dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            ResultSet resultSet = statement.executeQuery("SHOW TABLES");
            List<String> tableNames = fetchTableNames(resultSet);

            assertThat(tableNames,containsInAnyOrder("actors","movies","actors_movies"));
        }
    }

    @Test
    public void testSaveAndFindActorById() {
        Actor actorArnold = ActorMovieUtility.getActorsList().get(0);

        List<Actor> actorList = ActorMovieUtility.getActorsList();
        actorList.forEach(actorDao::saveNewActor);

        Actor actorFromDao = actorDao.findActorById(actorArnold.getId());

        assertNotNull(actorArnold.getId());
        assertEquals(actorArnold,actorFromDao);
    }

    @Test
    public void testSaveAndFindMovieByName() {
        Movie movieRedHeat = ActorMovieUtility.getMovieRedHeat();

        movieDao.saveNewMovie(movieRedHeat);

        Movie movieFromDB = movieDao.searchMovieByName("Red Heat");

        assertNotNull(movieFromDB);
        assertEquals(movieRedHeat,movieFromDB);
    }

    @Test
    public void testFindAllMovies() {
        List<Movie> movieList = ActorMovieUtility.getMoviesList();
        movieList.forEach(movie -> movieDao.saveNewMovie(movie));

        List<Movie> moviesListFromDB = movieDao.findAllMovies();

        assertEquals(movieList,moviesListFromDB);
    }

    @Test
    public void testLinkAndFindMovieByActor(){
        movieDao.saveNewMovie(ActorMovieUtility.getMovieRedHeat());

        linkAllActorsToMovie();

        List<String> arnoldMovies = movieDao.searchMoviesByActor("Arnold","Schwarzenegger")
                .stream().map(Movie::getName).collect(Collectors.toList());

        assertThat(arnoldMovies, containsInAnyOrder("The Expendables","Red Heat","Terminator 2: Judgment Day"));
    }

    private void linkAllActorsToMovie() {
        List<Movie> movieList = ActorMovieUtility.getMoviesList();
        List<Actor> actorList = ActorMovieUtility.getActorsList();
        actorDao.linkActorToMovieByIds(actorList.get(0),movieList.get(0));
        actorDao.linkActorToMovieByIds(actorList.get(0),movieList.get(3));
        actorDao.linkActorToMovieByIds(actorList.get(1),movieList.get(0));
        actorDao.linkActorToMovieByIds(actorList.get(1),movieList.get(1));
        actorDao.linkActorToMovieByIds(actorList.get(2),movieList.get(0));
        actorDao.linkActorToMovieByIds(actorList.get(2),movieList.get(4));
        actorDao.linkActorToMovieByIds(actorList.get(3),movieList.get(0));
        actorDao.linkActorToMovieByIds(actorList.get(3),movieList.get(4));
        actorDao.linkActorToMovieByIds(actorList.get(4),movieList.get(1));
        actorDao.linkActorToMovieByIds(actorList.get(5),movieList.get(1));
        actorDao.linkActorToMovieByIds(actorList.get(5),movieList.get(2));
        actorDao.linkActorToMovieByIds(actorList.get(6),movieList.get(2));
        actorDao.linkActorToMovieByIds(actorList.get(0),ActorMovieUtility.getMovieRedHeat());
    }

    private List<String> fetchTableNames(ResultSet resultSet) throws SQLException {
        List<String> tableNameList = new ArrayList<>();
        while (resultSet.next()) {
            tableNameList.add(resultSet.getString("table_name"));
        }
        return tableNameList;
    }

    private static void createActorMovieTable(DataSource h2dataSource) throws SQLException, IOException, URISyntaxException {
        try (Connection connection = h2dataSource.getConnection()) {
            Statement statement = connection.createStatement();

            if (statement.execute(getActorMovieCreationSqlFromFile())) {
                System.out.println("Table building statement was executed");
            }
        }
    }

    private static String getActorMovieCreationSqlFromFile() throws URISyntaxException, IOException {
        URL fileUrl = ActorMovieTest.class.getClassLoader().getResource("db/migration/createActorMovieTables.sql");
        Objects.requireNonNull(fileUrl);

        Stream<String> linesStream = Files.lines(Paths.get(fileUrl.toURI()));
        return linesStream.collect(Collectors.joining("\n"));
    }
}
