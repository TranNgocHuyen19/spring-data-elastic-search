package iuh.demo.elasticsearch.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.elasticsearch.client.ClientConfiguration;
import org.springframework.data.elasticsearch.client.elc.ElasticsearchConfiguration;
import org.springframework.data.elasticsearch.config.EnableElasticsearchAuditing;
import org.springframework.data.elasticsearch.repository.config.EnableElasticsearchRepositories;
import org.springframework.lang.NonNull;

@Configuration
@EnableElasticsearchRepositories(basePackages = "iuh.demo.elasticsearch.repository.elasticsearch")
@EnableElasticsearchAuditing
public class ElasticSearchConfiguration extends ElasticsearchConfiguration {

    @Value("${spring.elasticsearch.uris}")
    private String elasticSearchUrl;

    @Override
    @NonNull
    public ClientConfiguration clientConfiguration() {
        return ClientConfiguration.builder()
                .connectedTo(elasticSearchUrl)
                .build();
    }
}