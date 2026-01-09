package iuh.demo.elasticsearch.controller;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.dto.request.SendMessageRequest;
import iuh.demo.elasticsearch.dto.response.MessageSearchResult;
import iuh.demo.elasticsearch.model.mongodb.Message;
import iuh.demo.elasticsearch.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody SendMessageRequest request) {
        return ResponseEntity.ok(chatService.sendMessage(request));
    }

    @GetMapping("/history")
    public ResponseEntity<Page<Message>> getMessages(
            @RequestParam(required = false) String roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size
    ) {
        return ResponseEntity.ok(chatService.getMessages(roomId, page, size));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<MessageSearchResult>> searchMessages(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(chatService.searchMessage(request));
    }
}
