package com.elastic.poc.elasticPoc.util;

import com.elastic.poc.elasticPoc.dto.Movie;
import org.elasticsearch.action.search.SearchRequest;
import org.elasticsearch.index.query.QueryBuilder;
import org.elasticsearch.index.query.QueryBuilders;
import org.elasticsearch.search.builder.SearchSourceBuilder;

/**
 */
public class ESHelperUtil {

    public static SearchRequest movieSearchQueryById(Movie movie, String indexName, String typeName){
        SearchRequest searchRequest = new SearchRequest(indexName);
        searchRequest.types(typeName);
        SearchSourceBuilder searchSourceBuilder = new SearchSourceBuilder();
        searchSourceBuilder.query(QueryBuilders.idsQuery().addIds(movie.getId()));
        return searchRequest.source(searchSourceBuilder);
    }
}
