package iuh.demo.elasticsearch.dto.request;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SendMessageRequest {
    private String roomId;
    private String senderId;
    private String content;

}
