package iuh.demo.elasticsearch.service.index;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
@Slf4j
public class IndexServiceImpl implements IndexService {

    private final ElasticsearchOperations operations;
    private final List<Class<?>> documentClasses = List.of(UserDoc.class, MessageDoc.class);

    @Override
    @PostConstruct
    public void createIndices() {
        for (Class<?> clazz : documentClasses) {
            IndexOperations indexOps = operations.indexOps(clazz);
            if (!indexOps.exists()) {
                indexOps.create();
                indexOps.putMapping(indexOps.createMapping());
                log.info("ELASTICSEARCH: Đã tạo Index cho class: {}", clazz.getSimpleName());
            }
        }
    }
}