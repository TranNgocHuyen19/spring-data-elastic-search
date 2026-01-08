package iuh.demo.elasticsearch.model.mongodb;

import iuh.demo.elasticsearch.model.common.UserInfo;
import lombok.Builder;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.time.LocalDateTime;

@Document(collection = "messages")
@Data
@Builder
public class Message {
    @Id
    private String id;
    private String roomId;
    private UserInfo sender;
    private String content;
    private LocalDateTime createdAt;
}