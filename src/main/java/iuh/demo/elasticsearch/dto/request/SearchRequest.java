package iuh.demo.elasticsearch.dto.request;

import lombok.Getter;

import java.time.LocalDate;
import java.util.List;

@Getter
public class SearchRequest {
    private List<String> fields;
    private String searchTerm;

    private LocalDate fromDate;
    private LocalDate toDate;

    private int page;
    private int size;

    private String sortBy;
    private String sortOrder;
}
