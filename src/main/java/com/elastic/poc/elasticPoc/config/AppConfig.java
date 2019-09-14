package com.elastic.poc.elasticPoc.config;

import org.apache.http.Header;
import org.apache.http.HttpHost;
import org.apache.http.message.BasicHeader;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 */
@Configuration
public class AppConfig {

    @Value("${es.url}")
    private String[] esUrls;

    @Value("${es.port}")
    private int port;

    @Value("${es.socket.timeout}")
    private int esSocketTimeout;

    @Value("${es.connect.timeout}")
    private int esConnectTimeout;

    @Value("${es.retry.timeout}")
    private int esRetryCount;


    @Bean(name = "esRestClient")
    public RestHighLevelClient getElasticSearchClient(){
        HttpHost[] hosts = new HttpHost[esUrls.length];
        for(int i = 0; i < hosts.length; i++)
            hosts[i] = new HttpHost(esUrls[i], port);
        RestClientBuilder restClientBuilder = RestClient.builder(hosts)
                .setDefaultHeaders(new Header[]{new BasicHeader("Content-Type", "application/JSON")})
                .setRequestConfigCallback(requestConfigBuilder -> requestConfigBuilder
                .setConnectTimeout(esConnectTimeout).setSocketTimeout(esSocketTimeout)).setMaxRetryTimeoutMillis(esRetryCount);

        return new RestHighLevelClient(restClientBuilder);
    }
}
