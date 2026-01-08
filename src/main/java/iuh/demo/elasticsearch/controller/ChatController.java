package iuh.demo.elasticsearch.controller;

import iuh.demo.elasticsearch.model.Message;
import iuh.demo.elasticsearch.service.chat.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping("/send")
    public ResponseEntity<Message> sendMessage(@RequestBody Message message) {
        Message savedMessage = chatService.sendMessage(message);
        return ResponseEntity.ok(savedMessage);
    }

    @GetMapping("/history/{roomId}")
    public ResponseEntity<List<Message>> getHistory(@PathVariable String roomId) {
        return ResponseEntity.ok(chatService.getConversationHistory(roomId));
    }

    @GetMapping("/search")
    public ResponseEntity<Page<Message>> searchMessages(
            @RequestParam String keyword,
            @RequestParam(required = false) String roomId,
            @PageableDefault(size = 10) Pageable pageable
    ) {
        Page<Message> results = chatService.searchAdvanced(keyword, roomId, pageable);
        return ResponseEntity.ok(results);
    }
}