package iuh.demo.elasticsearch.service.user;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import iuh.demo.elasticsearch.model.mongodb.User;
import org.springframework.data.domain.Page;

public interface UserService {
    User createUser(User user);
    Page<UserDoc> searchUser(SearchRequest request);
}
