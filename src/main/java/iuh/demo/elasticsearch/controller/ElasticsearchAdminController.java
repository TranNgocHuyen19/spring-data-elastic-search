package iuh.demo.elasticsearch.controller;

import iuh.demo.elasticsearch.dto.response.SyncResponse;
import iuh.demo.elasticsearch.service.index.ElasticsearchAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/admin/es")
@RequiredArgsConstructor
public class ElasticsearchAdminController {

    private final ElasticsearchAdminService adminService;

    @PostMapping("/recreate-all")
    public ResponseEntity<String> recreateAll() {
        adminService.recreateAllIndexes();
        return ResponseEntity.ok("All Elasticsearch indexes recreated successfully");
    }

    @PostMapping("/sync/{indexName}")
    public ResponseEntity<SyncResponse> syncMongoToElasticsearch(
            @PathVariable String indexName
    ) {
        long count = adminService.syncMongoToElasticsearch(indexName);
        return ResponseEntity.ok(SyncResponse.builder()
                .indexName(indexName)
                .documentsSynced(count)
                .message("Sync " + indexName + " completed successfully")
                .build());
    }
}

