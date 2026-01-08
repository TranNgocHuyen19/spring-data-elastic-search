package iuh.demo.elasticsearch.configuration;

import org.springframework.context.annotation.Configuration;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;

@Configuration
@EnableMongoRepositories(basePackages = "iuh.demo.elasticsearch.repository.mongo")
public class MongoConfiguration {
}
