package iuh.demo.elasticsearch.service.index;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import iuh.demo.elasticsearch.model.mongodb.Message;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.repository.mongo.MessageRepository;
import iuh.demo.elasticsearch.repository.mongo.UserRepository;
import iuh.demo.elasticsearch.util.elasticsearch.Indices;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchAdminServiceImpl implements ElasticsearchAdminService {

    private final ElasticsearchOperations operations;
    private final MessageRepository messageRepository;
    private final UserRepository userRepository;

    private static final List<Class<?>> INDEX_CLASSES = List.of(
            UserDoc.class,
            MessageDoc.class
    );

    @Override
    public void recreateAllIndexes() {
        for (Class<?> clazz : INDEX_CLASSES) {
            recreateIndex(clazz);
        }
    }

    @Override
    public long syncMongoToElasticsearch(String indexName) {
        return switch (indexName.toLowerCase()) {
            case Indices.MESSAGE_INDEX -> syncMessages();
            case Indices.USER_INDEX -> syncUsers();
            case "all" -> syncMessages() + syncUsers();
            default -> throw new IllegalArgumentException(
                    "Unknown index: " + indexName + 
                    ". Valid values: " + Indices.MESSAGE_INDEX + ", " + Indices.USER_INDEX + ", all"
            );
        };
    }

    private long syncMessages() {
        recreateIndex(MessageDoc.class);

        List<Message> allMessages = messageRepository.findAll();

        List<MessageDoc> messageDocs = allMessages.stream()
                .map(message -> MessageDoc.builder()
                        .id(message.getId())
                        .roomId(message.getRoomId())
                        .content(message.getContent())
                        .sender(message.getSender())
                        .createdAt(message.getCreatedAt())
                        .build())
                .toList();

        if (!messageDocs.isEmpty()) {
            operations.save(messageDocs);
        }

        return messageDocs.size();
    }

    private long syncUsers() {
        recreateIndex(UserDoc.class);

        List<User> allUsers = userRepository.findAll();

        List<UserDoc> userDocs = allUsers.stream()
                .map(user -> UserDoc.builder()
                        .id(user.getId())
                        .name(user.getName())
                        .build())
                .toList();

        if (!userDocs.isEmpty()) {
            operations.save(userDocs);
        }

        return userDocs.size();
    }

    private void recreateIndex(Class<?> clazz) {
        IndexOperations indexOps = operations.indexOps(clazz);

        if (indexOps.exists()) {
            indexOps.delete();
        }

        indexOps.create();
        indexOps.putMapping(indexOps.createMapping());
    }
}
