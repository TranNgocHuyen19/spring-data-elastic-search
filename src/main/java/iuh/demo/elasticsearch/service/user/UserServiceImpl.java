package iuh.demo.elasticsearch.service.user;

import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import iuh.demo.elasticsearch.model.mongodb.User;
import iuh.demo.elasticsearch.repository.elasticsearch.UserSearchRepository;
import iuh.demo.elasticsearch.repository.mongo.UserRepository;
import iuh.demo.elasticsearch.util.elasticsearch.SearchUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.SearchHit;
import org.springframework.data.elasticsearch.core.SearchHits;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final ElasticsearchOperations elasticOperations;

    @Override
    public User createUser(User user) {
        User saved = userRepository.save(user);

        UserDoc doc = UserDoc.builder()
                .id(saved.getId())
                .name(saved.getName())
                .build();
        elasticOperations.save(doc);

        return saved;
    }

    @Override
    public Page<UserDoc> searchUser(SearchRequest request) {
        NativeQuery query = SearchUtil.buildNativeQuery(request);
        if (query == null) return Page.empty();

        SearchHits<UserDoc> hits = elasticOperations.search(query, UserDoc.class);
        List<UserDoc> list = hits.stream().map(SearchHit::getContent).toList();

        return new PageImpl<>(list, query.getPageable(), hits.getTotalHits());
    }
}