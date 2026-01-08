package iuh.demo.elasticsearch.service.chat;

import iuh.demo.elasticsearch.model.Message;
import iuh.demo.elasticsearch.repository.mongo.MessageRepository;
import iuh.demo.elasticsearch.repository.elasticsearch.MessageSearchRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final MessageRepository messageRepository;
    private final MessageSearchRepository messageSearchRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public Message sendMessage(Message message) {
        Message savedMessage = messageRepository.save(message);

        try {
            messageSearchRepository.save(savedMessage);
        } catch (Exception e) {
            System.err.println("Lỗi sync Elastic: " + e.getMessage());
        }

        return savedMessage;
    }

    @Override
    public Message findById(String id) {
        return messageRepository.findById(id).orElseThrow(() -> new RuntimeException("Message not found"));
    }

    @Override
    public List<Message> searchMessages(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return List.of();
        }
        return messageSearchRepository.findByContentContaining(keyword);
    }

    @Override
    public Page<Message> searchAdvanced(String keyword, String roomId, Pageable pageable) {
       return null;
    }

    @Override
    public List<Message> getConversationHistory(String roomId) {
        return messageRepository.findByRoomId(roomId);
    }
}