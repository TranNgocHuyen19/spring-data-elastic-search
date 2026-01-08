package iuh.demo.elasticsearch.controller;

import iuh.demo.elasticsearch.service.index.ElasticsearchAdminService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
}
