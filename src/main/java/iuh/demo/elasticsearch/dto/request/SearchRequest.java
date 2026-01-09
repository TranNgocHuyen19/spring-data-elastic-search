package iuh.demo.elasticsearch.dto.request;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
public class SearchRequest {

    private String searchTerm;

    private List<String> searchFields;

    private Map<String, String> termFilters;

    private Map<String, RangeFilter> rangeFilters;

    private int page;

    private int size;

    private String sortBy;

    private String sortOrder;

    @Data
    @Builder
    public static class RangeFilter {
        private String from;
        private String to;
    }
}
