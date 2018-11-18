package com.grigus.dao;

import com.grigus.model.Actor;
import com.grigus.model.Movie;

public interface ActorDao {
    void saveNewActor(Actor actor);
    Actor findActorById (Long id);
    void linkActorToMovieByIds (Actor actor, Movie movie);
}
