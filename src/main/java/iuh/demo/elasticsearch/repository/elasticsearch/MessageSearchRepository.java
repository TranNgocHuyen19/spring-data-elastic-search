package iuh.demo.elasticsearch.repository.elasticsearch;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;

import java.util.List;

public interface MessageSearchRepository extends ElasticsearchRepository<MessageDoc, String> {
    List<MessageDoc> findByContentContaining(String content);
}
