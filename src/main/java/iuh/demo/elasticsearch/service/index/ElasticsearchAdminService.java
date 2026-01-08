package iuh.demo.elasticsearch.service.index;

public interface ElasticsearchAdminService {
    void recreateAllIndexes();
    
    long syncMongoToElasticsearch(String indexName);
}
