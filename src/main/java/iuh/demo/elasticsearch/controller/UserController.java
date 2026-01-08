package iuh.demo.elasticsearch.controller;


import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.service.user.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @PostMapping
    public ResponseEntity<User> createUser(@RequestBody User user) {
        return ResponseEntity.ok(userService.createUser(user));
    }

    @PostMapping("/search")
    public ResponseEntity<Page<UserDoc>> searchUser(@RequestBody SearchRequest request) {
        return ResponseEntity.ok(userService.searchUser(request));
    }
}
