package com.elastic.poc.elasticPoc.service;

import com.elastic.poc.elasticPoc.dto.Movie;
import com.elastic.poc.elasticPoc.util.ESHelperUtil;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.elasticsearch.action.bulk.BulkRequest;
import org.elasticsearch.action.index.IndexRequest;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.action.search.SearchResponse;
import org.elasticsearch.client.RequestOptions;
import org.elasticsearch.client.RestHighLevelClient;
import org.elasticsearch.common.xcontent.XContentType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 */
@Service
public class MovieService {

    private static final Logger LOG = LoggerFactory.getLogger(MovieService.class);

    @Value("${es.bulk.limit}")
    private int bulkLimit;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final RestHighLevelClient esRestClient;

    @Autowired
    public MovieService(@Qualifier("esRestClient") RestHighLevelClient esRestClient) {
        this.esRestClient = esRestClient;
    }

    public List<Movie> searchMovieById(Movie movie) {
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

    public void insertIntoES(final String index, final String type) {
        try {
            int count = 0;
            List<Movie> movies = new ArrayList<>();
            InputStream inputStream = MovieService.class.getClassLoader().getResourceAsStream("movies_bulk.json");
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
            String line = bufferedReader.readLine();
            while (line != null) {
                Movie movie = objectMapper.readValue(line, Movie.class);
                movies.add(movie);
                line = bufferedReader.readLine();
            }
            BulkRequest bulkRequest = new BulkRequest();
            for (Movie movie : movies) {
                if (count < bulkLimit) {
                    count++;
                    bulkRequest.add(new IndexRequest(index, type, movie.getId())
                            .source(objectMapper.writeValueAsString(movie), XContentType.JSON));
                } else {
                    esRestClient.bulk(bulkRequest, RequestOptions.DEFAULT);
                    bulkRequest = new BulkRequest();
                    count = 0;
                }
            }
            if (count > 0) {
                esRestClient.bulk(bulkRequest, RequestOptions.DEFAULT);
            }
        } catch (IOException ex) {
            LOG.error("Error while reading movies file {}", ex);
        }


    }
}
