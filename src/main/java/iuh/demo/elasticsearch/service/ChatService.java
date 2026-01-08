package iuh.demo.elasticsearch.service;

import iuh.demo.elasticsearch.model.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;

public interface ChatService {

    Message sendMessage(Message message);

    Message findById(String id);

    List<Message> searchMessages(String keyword);

    List<Message> getConversationHistory(String roomId);

    Page<Message> searchAdvanced(String keyword, String roomId, Pageable pageable);
}
