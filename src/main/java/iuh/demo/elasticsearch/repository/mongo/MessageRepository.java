package iuh.demo.elasticsearch.repository.mongo;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    Page<Message> findByRoomId(String roomId, Pageable pageable);
}
