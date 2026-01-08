package iuh.demo.elasticsearch.configuration;

import iuh.demo.elasticsearch.dto.request.SendMessageRequest;
import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.repository.mongo.UserRepository;
import iuh.demo.elasticsearch.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitApp implements CommandLineRunner {

    private final ChatService chatService;
    private final UserRepository userRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    private static final String[] SAMPLE_CONTENTS = {
            "Huyền", "Huyen", "hUYỀN", "Huyềnn", "Huyeefn",
            "H u y ề n", "H.u.y.ề.n", "Huyển", "Hyền", "Huỳen",
            "Xin chào Huyền nhé", "Tìm bạn tên là H uyen gấp",
            "Huyềnnnnn", "@Huyền@", "Huyề_n"
    };

    @Override
    public void run(String... args) throws Exception {

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Tran Thi B")
                .build();

        userRepository.save(user);

        for (String content : SAMPLE_CONTENTS) {

            SendMessageRequest request = SendMessageRequest.builder()
                    .roomId("1")
                    .content(content)
                    .senderId(user.getId())
                    .build();

            Message savedMessage = chatService.sendMessage(request);

            syncToElasticsearch(savedMessage);
        }

    }

    private void syncToElasticsearch(Message message) {
        MessageDoc messageDoc = MessageDoc.builder()
                .id(message.getId())
                .roomId(message.getRoomId())
                .content(message.getContent())
                .sender(message.getSender())
                .createdAt(message.getCreatedAt())
                .build();

        elasticsearchOperations.save(messageDoc);
    }
}