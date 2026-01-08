package iuh.demo.elasticsearch.repository.elasticsearch;

import iuh.demo.elasticsearch.model.User;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserRepository extends ElasticsearchRepository<User, String> {
}
