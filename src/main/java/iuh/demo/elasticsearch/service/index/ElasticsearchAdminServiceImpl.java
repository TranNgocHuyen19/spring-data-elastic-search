package iuh.demo.elasticsearch.service.index;

import iuh.demo.elasticsearch.model.elasticsearch.MessageDoc;
import iuh.demo.elasticsearch.model.elasticsearch.UserDoc;
import lombok.RequiredArgsConstructor;
import org.springframework.data.elasticsearch.core.ElasticsearchOperations;
import org.springframework.data.elasticsearch.core.IndexOperations;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ElasticsearchAdminServiceImpl implements ElasticsearchAdminService {

    private final ElasticsearchOperations operations;

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

    private void recreateIndex(Class<?> clazz) {
        IndexOperations indexOps = operations.indexOps(clazz);

        if (indexOps.exists()) {
            indexOps.delete();
        }

        indexOps.create();
        indexOps.putMapping(indexOps.createMapping());
    }
}
