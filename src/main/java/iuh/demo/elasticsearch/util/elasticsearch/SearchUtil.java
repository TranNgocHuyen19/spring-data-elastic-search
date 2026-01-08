package iuh.demo.elasticsearch.util.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Operator;
import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.elasticsearch._types.query_dsl.TextQueryType;
import iuh.demo.elasticsearch.dto.request.SearchRequest;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.List;

@NoArgsConstructor
public final class SearchUtil {

    public static Query getQuery(SearchRequest request) {
        if (request == null || !StringUtils.hasText(request.getSearchTerm())) {
            return null;
        }

        List<String> fields = request.getFields();
        if (CollectionUtils.isEmpty(fields)) {
            return null;
        }

        if (fields.size() == 1) {
            return Query.of(q -> q.match(m -> m
                    .field(fields.get(0))
                    .query(request.getSearchTerm())
                    .operator(Operator.And)));
        }

        return Query.of(q -> q.multiMatch(m -> m
                .query(request.getSearchTerm())
                .fields(fields)
                .type(TextQueryType.CrossFields)
                .operator(Operator.And)));
    }

    public static NativeQuery buildNativeQuery(SearchRequest request) {
        Query query = getQuery(request);
        if (query == null) return null;

        Pageable pageable = PageRequest.of(request.getPage(), request.getSize(), getSort(request));

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .build();
    }

    private static Sort getSort(SearchRequest request) {
        if (!StringUtils.hasText(request.getSortBy())) {
            return Sort.unsorted();
        }

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortOrder())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, request.getSortBy());
    }

}