package iuh.demo.elasticsearch.service.chat;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.dto.request.SendMessageRequest;
import iuh.demo.elasticsearch.dto.response.MessageSearchResult;
import iuh.demo.elasticsearch.model.mongodb.Message;
import org.springframework.data.domain.Page;

public interface ChatService {
    Message sendMessage(SendMessageRequest request);

    Page<Message> getMessages(String roomId, int page, int size);

    Page<MessageSearchResult> searchMessage(SearchRequest request);
}

