package iuh.demo.elasticsearch.dto.request;

import lombok.Getter;

import java.util.List;

@Getter
public class SearchRequest {
    private List<String> fields;
    private String searchTerm;

    private int page;
    private int size;

    private String sortBy;
    private String sortOrder;
}
