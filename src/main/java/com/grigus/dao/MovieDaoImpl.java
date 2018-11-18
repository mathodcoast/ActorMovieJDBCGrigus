package com.grigus.dao;

import com.grigus.Exeption.DaoOperationExeption;
import com.grigus.Utility.ActorMovieUtility;
import com.grigus.model.Movie;

import javax.sql.DataSource;
import java.sql.*;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class MovieDaoImpl implements MovieDao {
    private static final String INSERT_MOVIE_TO_DB_SQL = "INSERT INTO movies (name, duration, release_date) VALUES (?, ?, ?)";
    private static final String FIND_MOVIE_BY_NAME_SQL = "SELECT * FROM movies WHERE name = ?";
    private static final String SELECT_ALL_MOVIES_SQL = "SELECT * FROM movies;";
    private static final String SELECT_ACTOR_ID_BY_NAME_SQL = "SELECT id FROM actors WHERE first_name = ? AND last_name = ?;";
    private static final String SELECT_MOVIE_ID_BY_ACTOR_ID_SQL = "SELECT movie_id FROM actors_movies WHERE actor_id = ?";
    private static final String SELECT_MOVIE_BY_ID = "SELECT * FROM  movies WHERE id = ?";
    private DataSource dataSource = ActorMovieUtility.getDataSource();

    @Override
    public void saveNewMovie(Movie movie) {
        Objects.requireNonNull(movie,"Movie fore saving is null");
        if (movie.getId() == null) {
            try (Connection connection = dataSource.getConnection()) {
                saveMovieToDB(movie,connection);
            } catch (SQLException e) {
                throw new DaoOperationExeption("Error of Movie saving", e);
            }
        } else {
            System.out.println(String.format("Movie %s was already saved.", movie.getName()));
        }
    }

    private void saveMovieToDB(Movie movie,Connection connection) throws SQLException {
        PreparedStatement preparedStatementForMovieSave = setupPreparedStatementOfInsertingMovie(movie,connection);
        executeMovieSaving(preparedStatementForMovieSave);
        getGeneratedID(movie,preparedStatementForMovieSave);
    }

    private void getGeneratedID(Movie movie,PreparedStatement preparedStatementForMovieSave) throws SQLException {
        ResultSet idResultSet = preparedStatementForMovieSave.getGeneratedKeys();
        if (idResultSet.next()) {
            movie.setId(idResultSet.getLong("id"));
        } else {
            throw new DaoOperationExeption("Id wasn't obtained");
        }
    }

    private void executeMovieSaving(PreparedStatement preparedStatementForMovieSave) throws SQLException {
        int rowsAffected = preparedStatementForMovieSave.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationExeption("Movie wasn't saved in DB");
        }
    }

    private PreparedStatement setupPreparedStatementOfInsertingMovie(Movie movie,Connection connection) throws SQLException {
        PreparedStatement preparedStatementForMovieSave = connection.prepareStatement(INSERT_MOVIE_TO_DB_SQL,Statement.RETURN_GENERATED_KEYS);
        preparedStatementForMovieSave.setString(1,movie.getName());
        preparedStatementForMovieSave.setDouble(2,movie.getDuration().toMinutes());
        preparedStatementForMovieSave.setDate(3,Date.valueOf(movie.getReleaseDate()));
        return preparedStatementForMovieSave;
    }

    @Override
    public List<Movie> findAllMovies() {
        List<Movie> moviesList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            return getAllMoviesFromDB(moviesList,connection);
        } catch (SQLException e) {
            throw new DaoOperationExeption("Error of finding all films. ", e);
        }
    }

    private List<Movie> getAllMoviesFromDB(List<Movie> moviesList,Connection connection) throws SQLException {
        Statement statementFindAllMovies = connection.createStatement();
        ResultSet resultSet = statementFindAllMovies.executeQuery(SELECT_ALL_MOVIES_SQL);
        while (resultSet.next()) {
            Movie movie = new Movie();
            parseMovieFromResultSet(movie,resultSet);
            moviesList.add(movie);
        }
        return moviesList;
    }

    private Movie parseMovieFromResultSet(Movie movie,ResultSet resultSet) throws SQLException {
        movie.setId(resultSet.getLong("id"));
        movie.setName(resultSet.getString("name"));
        movie.setDuration(Duration.ofMinutes(resultSet.getLong("duration")));
        movie.setReleaseDate(resultSet.getDate("release_date").toLocalDate());
        return movie;
    }

    @Override
    public Movie searchMovieByName(String name) {
        Movie movie = new Movie();
        try (Connection connection = dataSource.getConnection()) {
            ResultSet resultSet = setupConnectionToResultSet(name,connection);
            return parseMovieFromResultSet(movie,resultSet);
        } catch (SQLException e) {
            throw new DaoOperationExeption("Error searching Movie by name. ", e);
        }
    }

    private ResultSet setupConnectionToResultSet(String name,Connection connection) throws SQLException {
        PreparedStatement preparedStatementFindByName = connection.prepareStatement(FIND_MOVIE_BY_NAME_SQL);
        preparedStatementFindByName.setString(1,name);
        ResultSet resultSet = preparedStatementFindByName.executeQuery();
        resultSet.next();
        return resultSet;
    }

    @Override
    public List<Movie> searchMoviesByActor(String firstName,String lastName) {
        List<Movie> moviesList = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()) {
            return getMoviesByActorName(firstName,lastName,moviesList,connection);
        } catch (SQLException e) {
            throw new DaoOperationExeption("Error of finding movie by actor first and last names. ",e);
        }
    }

    private List<Movie> getMoviesByActorName(String firstName,String lastName,List<Movie> moviesList,Connection connection) throws SQLException {
        long actorId = getActorIdByName(firstName,lastName,connection);
        List<Long> moviesIdList = getMoviesIdList(connection,actorId);
        return getMoviesByIdsList(moviesList,connection,moviesIdList);
    }

    private List<Movie> getMoviesByIdsList(List<Movie> moviesList,Connection connection,List<Long> moviesIdList) throws SQLException {
        PreparedStatement preparedStatementGetMovieByMovieId = connection.prepareStatement(SELECT_MOVIE_BY_ID);
        for (long id : moviesIdList) {
            preparedStatementGetMovieByMovieId.setLong(1,id);
            getListOfMoviesFromDB(moviesList,preparedStatementGetMovieByMovieId);
        }
        return moviesList;
    }

    private void getListOfMoviesFromDB(List<Movie> moviesList,PreparedStatement preparedStatementToGetMovies) throws SQLException {
        ResultSet resultSetMovieByID = preparedStatementToGetMovies.executeQuery();
        if (resultSetMovieByID.next()) {
            Movie movie = new Movie();
            parseMovieFromResultSet(movie,resultSetMovieByID);
            moviesList.add(movie);
        } else {
            throw new DaoOperationExeption("Movie by ID is not exist in DB.");
        }
    }

    private List<Long> getMoviesIdList(Connection connection,long actorId) throws SQLException {
        PreparedStatement preparedStatementGetMovieIdByActorId = connection.prepareStatement(SELECT_MOVIE_ID_BY_ACTOR_ID_SQL);
        preparedStatementGetMovieIdByActorId.setLong(1,actorId);
        ResultSet resultSetMoviesIds = preparedStatementGetMovieIdByActorId.executeQuery();
        List<Long> moviesIdList = new ArrayList<>();
        while (resultSetMoviesIds.next()) {
            moviesIdList.add(resultSetMoviesIds.getLong("movie_id"));
        }
        return moviesIdList;
    }

    private long getActorIdByName(String firstName,String lastName,Connection connection) throws SQLException {
        System.out.println("Start");
        PreparedStatement preparedStatementFindActorIdByName = connection.prepareStatement(SELECT_ACTOR_ID_BY_NAME_SQL);
        preparedStatementFindActorIdByName.setString(1,firstName);
        preparedStatementFindActorIdByName.setString(2,lastName);
        ResultSet resultSet = preparedStatementFindActorIdByName.executeQuery();
        if (resultSet.next()) {
            return resultSet.getLong("id");
        } else {
            System.out.println("Error");
            throw new DaoOperationExeption("Actor ID by name wasn't found");
        }
    }
}
