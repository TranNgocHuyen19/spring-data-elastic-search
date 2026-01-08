package iuh.demo.elasticsearch.service.chat;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.dto.request.SendMessageRequest;
import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ChatService {
    Message sendMessage(SendMessageRequest request);

    Page<Message> getMessages(String roomId, int page, int size);

    Page<MessageDoc> searchMessage(SearchRequest request);
}
