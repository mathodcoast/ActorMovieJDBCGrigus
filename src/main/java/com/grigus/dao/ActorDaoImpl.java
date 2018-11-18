package com.grigus.dao;

import com.grigus.Exeption.DaoOperationExeption;
import com.grigus.Utility.ActorMovieUtility;
import com.grigus.model.Actor;
import com.grigus.model.Movie;

import javax.sql.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.Objects;

public class ActorDaoImpl implements ActorDao {
    private static final String SAVE_ACTOR_IN_DB_SQL = "INSERT INTO actors (first_name, last_name, birthday) VALUES (?, ?, ?);";
    private static final String OBTAIN_ACTOR_BY_ID = "SELECT * FROM actors WHERE id = ?";
    private static final String LINK_ACTOR_AND_MOVIE_SQL = "INSERT INTO actors_movies (actor_id, movie_id) VALUES (?,?)";
    private DataSource dataSource = ActorMovieUtility.getDataSource();

    @Override
    public void saveNewActor(Actor actor) {
        Objects.requireNonNull(actor);
        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement savePreparedStatement = connection.prepareStatement(SAVE_ACTOR_IN_DB_SQL,Statement.RETURN_GENERATED_KEYS);
            savePreparedStatement.setString(1,actor.getFirstName());
            savePreparedStatement.setString(2,actor.getLastName());
            savePreparedStatement.setDate(3,Date.valueOf(actor.getBirthday()));

            executeUpdate(savePreparedStatement);

            obtainIdOfSavedActor(actor,savePreparedStatement);

        } catch (SQLException e) {
            throw new DaoOperationExeption("Actor data wasn't saved");
        }
    }

    private void obtainIdOfSavedActor(Actor actor,PreparedStatement savePreparedStatement) throws SQLException {
        ResultSet idResultSet = savePreparedStatement.getGeneratedKeys();
        if (idResultSet.next()) {
            actor.setId(idResultSet.getLong("id"));
        } else {
            throw new DaoOperationExeption("Cannot obtain actor ID");
        }
    }

    private void executeUpdate(PreparedStatement savePreparedStatement) throws SQLException {
        int rowsAffected = savePreparedStatement.executeUpdate();
        if (rowsAffected == 0) {
            throw new DaoOperationExeption("Update Error");
        }
    }

    @Override
    public Actor findActorById(Long id) {
        Actor actorFromDB = new Actor();
        try (Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatementByID = connection.prepareStatement(OBTAIN_ACTOR_BY_ID);
            preparedStatementByID.setLong(1,id);
            ResultSet resultSet = preparedStatementByID.executeQuery();
            if (resultSet.next()){
                actorFromDB.setId(resultSet.getLong("id"));
                actorFromDB.setFirstName(resultSet.getString("first_name"));
                actorFromDB.setLastName(resultSet.getString("last_name"));
                actorFromDB.setBirthday(resultSet.getDate("birthday").toLocalDate());
            } else {
                throw new DaoOperationExeption("Actor by ID not found");
            }
            return actorFromDB;
        } catch (SQLException e){
            throw new DaoOperationExeption("Error of finding by ID",e);
        }
    }

    @Override
    public void linkActorToMovieByIds(Actor actor,Movie movie) {
        Objects.requireNonNull(actor.getId(),"Missing Actor ID");
        Objects.requireNonNull(movie.getId(),"Missing Movie ID");

        try(Connection connection = dataSource.getConnection()){
            PreparedStatement preparedStatementForLinkActorAndMovie = getPreparedStatement(actor,movie,connection);
            executeUpdate(preparedStatementForLinkActorAndMovie);
        }catch (SQLException e){
            throw new DaoOperationExeption("Error of actor to movie linking.", e);
        }
    }

    private PreparedStatement getPreparedStatement(Actor actor,Movie movie,Connection connection) throws SQLException {
        PreparedStatement preparedStatementForLinkActorAndMovie = connection.prepareStatement(LINK_ACTOR_AND_MOVIE_SQL);
        preparedStatementForLinkActorAndMovie.setLong(1,actor.getId());
        preparedStatementForLinkActorAndMovie.setLong(2,movie.getId());
        return preparedStatementForLinkActorAndMovie;
    }
}
