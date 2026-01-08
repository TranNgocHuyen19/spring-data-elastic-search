package iuh.demo.elasticsearch.service.chat;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.dto.request.SendMessageRequest;
import iuh.demo.elasticsearch.model.common.UserInfo;
import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.repository.mongo.MessageRepository;
import iuh.demo.elasticsearch.repository.mongo.UserRepository;
import iuh.demo.elasticsearch.util.elasticsearch.SearchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatServiceImpl implements ChatService {

    private final ElasticsearchOperations elasticOperations;

    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    @Override
    public Message sendMessage(SendMessageRequest request) {
        User user = userRepository.findById(request.getSenderId())
                .orElseThrow(() -> new RuntimeException("User not found"));

        UserInfo senderInfo = UserInfo.builder()
                .id(user.getId())
                .name(user.getName())
                .build();

        Message message = Message.builder()
                .roomId(request.getRoomId())
                .sender(senderInfo)
                .content(request.getContent())
                .createdAt(LocalDateTime.now())
                .build();

        return messageRepository.save(message);
    }

    @Override
    public Page<Message> getMessages(String roomId, int page, int size) {
        Pageable pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"));

        if (roomId == null || roomId.isEmpty()) {
            return messageRepository.findAll(pageable);
        }
        return messageRepository.findByRoomId(roomId, pageable);
    }

    @Override
    public Page<MessageDoc> searchMessage(SearchRequest request) {
        NativeQuery query = SearchUtil.buildNativeQuery(request);
        if (query == null) return Page.empty();

        SearchHits<MessageDoc> hits = elasticOperations.search(query, MessageDoc.class);
        List<MessageDoc> list = hits.stream().map(SearchHit::getContent).toList();

        return new PageImpl<>(list, query.getPageable(), hits.getTotalHits());
    }
}