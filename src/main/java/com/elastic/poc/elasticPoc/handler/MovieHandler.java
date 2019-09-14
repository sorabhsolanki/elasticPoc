package com.elastic.poc.elasticPoc.handler;

import com.elastic.poc.elasticPoc.dto.Movie;
import com.elastic.poc.elasticPoc.service.MovieService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.List;

/**
 */
@Component
public class MovieHandler {

    private static final Logger LOG = LoggerFactory.getLogger(MovieHandler.class);

    private final MovieService movieService;

    @Autowired
    public MovieHandler(MovieService movieService) {
        this.movieService = movieService;
    }

    @PostConstruct
    public void init(){
        searchMovieForId("1924");
    }

    public void searchMovieForId(final String movieId){
        Movie movie = new Movie();
        movie.setId(movieId);
        List<Movie> movies = movieService.searchMovieById(movie);
        for(Movie movieObj : movies){
            LOG.info("Movie {} ", movieObj);
        }
    }
}
