package iuh.demo.elasticsearch.repository.mongo;

import iuh.demo.elasticsearch.model.Message;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface MessageRepository extends MongoRepository<Message, String> {
    List<Message> findByRoomId(String roomId);
}
