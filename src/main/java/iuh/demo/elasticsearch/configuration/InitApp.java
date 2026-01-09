package iuh.demo.elasticsearch.configuration;

import iuh.demo.elasticsearch.model.common.UserInfo;
import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.repository.mongo.MessageRepository;
import iuh.demo.elasticsearch.repository.mongo.UserRepository;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
@RequiredArgsConstructor
@Slf4j
public class InitApp implements CommandLineRunner {

    private final UserRepository userRepository;
    private final MessageRepository messageRepository;
    private final ElasticsearchOperations elasticsearchOperations;

    @Override
    public void run(String... args) {

        var indexOps = elasticsearchOperations.indexOps(MessageDoc.class);
        if (indexOps.exists()) {
            indexOps.delete();
        }
        
        indexOps.create();
        indexOps.putMapping();

        User user = User.builder()
                .id(UUID.randomUUID().toString())
                .name("Tran Thi B")
                .build();
        userRepository.save(user);

        List<RawMessage> sampleData = getRawMessages();

        for (RawMessage raw : sampleData) {
            String msgId = UUID.randomUUID().toString();
            UserInfo senderInfo = new UserInfo(user.getId(), user.getName());

            Message message = Message.builder()
                    .id(msgId)
                    .roomId(raw.getRoomId())
                    .content(raw.getContent())
                    .sender(senderInfo)
                    .createdAt(raw.getCreatedAt())
                    .build();
            messageRepository.save(message);

            MessageDoc messageDoc = MessageDoc.builder()
                    .id(msgId)
                    .roomId(raw.getRoomId())
                    .content(raw.getContent())
                    .sender(senderInfo)
                    .createdAt(raw.getCreatedAt())
                    .build();

            elasticsearchOperations.save(messageDoc);
        }

    }

    private List<RawMessage> getRawMessages() {
        List<RawMessage> list = new ArrayList<>();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

        list.add(new RawMessage("1", "Huyền ơi, nay có đi làm không?", LocalDateTime.parse("2026-01-01T08:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huyen check tin nhắn gấp nha", LocalDateTime.parse("2026-01-01T09:00:00.000", formatter)));
        list.add(new RawMessage("1", "Chào bạn Huyền xinh đẹp", LocalDateTime.parse("2026-01-01T10:00:00.000", formatter)));
        list.add(new RawMessage("1", "ìm contact của Huyen gap", LocalDateTime.parse("2026-01-01T11:00:00.000", formatter)));
        list.add(new RawMessage("1", "Alo Huyền, nghe máy đi", LocalDateTime.parse("2026-01-01T12:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huyển đi họp chưa", LocalDateTime.parse("2026-01-01T13:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huyềnn nộp báo cáo chưa", LocalDateTime.parse("2026-01-01T14:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huyeefn ơi (lỗi gõ Telex)", LocalDateTime.parse("2026-01-01T15:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huỳen xem giúp tớ cái bug này", LocalDateTime.parse("2026-01-01T16:00:00.000", formatter)));
        list.add(new RawMessage("1", "Hyền đâu rồi nhỉ (thiếu chữ u)", LocalDateTime.parse("2026-01-01T17:00:00.000", formatter)));
        list.add(new RawMessage("1", "Huyềb (gõ nhầm n thành b)", LocalDateTime.parse("2026-01-01T18:00:00.000", formatter)));
        list.add(new RawMessage("1", "H.u.y.ề.n ơi H u y ề n (có dấu cách)", LocalDateTime.parse("2026-01-01T19:00:00.000", formatter)));
        list.add(new RawMessage("1", "@Huyền@ fix bug gấp", LocalDateTime.parse("2026-01-01T20:00:00.000", formatter)));
        list.add(new RawMessage("1", "#Huyen team lead", LocalDateTime.parse("2026-01-01T21:00:00.000", formatter)));
        list.add(new RawMessage("1", "ạn tên H uyen đúng không", LocalDateTime.parse("2026-01-01T22:00:00.000", formatter)));
        list.add(new RawMessage("1", "ạn tên Huyennnnn đúng không", LocalDateTime.parse("2026-01-01T22:02:00.000", formatter)));
        list.add(new RawMessage("1", "Huyennnnnnnn nhiều chữ n", LocalDateTime.parse("2026-01-01T22:03:00.000", formatter)));
        list.add(new RawMessage("1", "HHuyenn gõ nhầm đầu", LocalDateTime.parse("2026-01-01T22:04:00.000", formatter)));
        list.add(new RawMessage("1", "Huuyeenn gõ nhầm giữa", LocalDateTime.parse("2026-01-01T22:05:00.000", formatter)));
        list.add(new RawMessage("1", "huyên sai dấu", LocalDateTime.parse("2026-01-01T22:06:00.000", formatter)));
        list.add(new RawMessage("1", "huỳen sai vị trí dấu", LocalDateTime.parse("2026-01-01T22:07:00.000", formatter)));
        list.add(new RawMessage("1", "huy thiếu chữ", LocalDateTime.parse("2026-01-01T22:08:00.000", formatter)));
        list.add(new RawMessage("1", "hu quá ngắn", LocalDateTime.parse("2026-01-01T22:09:00.000", formatter)));
        list.add(new RawMessage("1", "hui gõ nhầm", LocalDateTime.parse("2026-01-01T22:10:00.000", formatter)));
        list.add(new RawMessage("1", "huyeefn lỗi telex khác", LocalDateTime.parse("2026-01-01T22:11:00.000", formatter)));

        return list;
    }

    @Getter
    @AllArgsConstructor
    private static class RawMessage {
        private String roomId;
        private String content;
        private LocalDateTime createdAt;
    }
}