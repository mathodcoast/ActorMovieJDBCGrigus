package com.grigus.dao;

import com.grigus.model.Movie;

import java.util.List;

public interface MovieDao {
    void saveNewMovie(Movie movie);
    List<Movie> findAllMovies();
    Movie searchMovieByName (String name);
    List<Movie> searchMoviesByActor(String firstName,String lastName);
}
