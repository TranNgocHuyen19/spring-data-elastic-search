package iuh.demo.elasticsearch.repository.elasticsearch;

import iuh.demo.elasticsearch.model.Message;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageSearchRepository extends ElasticsearchRepository<Message, String> {
    List<Message> findByContentContaining(String content);
}
