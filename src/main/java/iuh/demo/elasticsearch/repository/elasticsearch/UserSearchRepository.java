package iuh.demo.elasticsearch.repository.elasticsearch;

import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

public interface UserSearchRepository extends ElasticsearchRepository<UserDoc, String> {
}
