package com.elastic.poc.elasticPoc.service;

import com.elastic.poc.elasticPoc.dto.Movie;
import com.elastic.poc.elasticPoc.util.ESHelperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    private final ObjectMapper objectMapper = new ObjectMapper();

    private final RestHighLevelClient esRestClient;

    @Autowired
    public MovieService(@Qualifier("esRestClient") RestHighLevelClient esRestClient) {
        this.esRestClient = esRestClient;
    }

    public List<Movie> searchMovieById(Movie movie){
        LOG.info("Searching movie for id {}", movie.getId());
        List<Movie> movies = new ArrayList<>();
        try {
            SearchRequest searchRequest = ESHelperUtil.movieSearchQueryById(movie, "movies", "movie");
                SearchResponse searchResponse = esRestClient.search(searchRequest, RequestOptions.DEFAULT);
            searchResponse.getHits().forEach(hit -> {
                try {
                    movies.add(objectMapper.readValue(hit.getSourceAsString(), Movie.class));
                } catch (IOException e) {
                    LOG.error("Error while deserialization {}", e);
                }
            });
        } catch (Exception e) {
            LOG.error("Error while fetching record from ES {}", e);
        }
        return movies;
    }
}
