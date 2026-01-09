package iuh.demo.elasticsearch.dto.response;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MessageSearchResult {
    
    private MessageDoc message;
    private Map<String, List<String>> highlights;
}
