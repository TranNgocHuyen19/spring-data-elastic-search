package iuh.demo.elasticsearch.util.elasticsearch;

import co.elastic.clients.elasticsearch._types.query_dsl.Query;
import co.elastic.clients.json.JsonData;
import iuh.demo.elasticsearch.dto.request.SearchRequest;
import iuh.demo.elasticsearch.dto.request.SearchRequest.RangeFilter;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.elasticsearch.client.elc.NativeQuery;
import org.springframework.data.elasticsearch.core.query.HighlightQuery;
import org.springframework.data.elasticsearch.core.query.highlight.Highlight;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightField;
import org.springframework.data.elasticsearch.core.query.highlight.HighlightParameters;
import org.springframework.util.CollectionUtils;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@NoArgsConstructor
public final class SearchUtil {

    private static final String DEFAULT_SORT_FIELD = "createdAt";

    public static NativeQuery buildNativeQuery(SearchRequest request) {
        if (request == null) return null;

        Query query = buildQuery(request);

        Pageable pageable = PageRequest.of(
                Math.max(request.getPage(), 0),
                Math.max(request.getSize(), 10),
                Sort.by(Sort.Direction.DESC, DEFAULT_SORT_FIELD)
        );

        List<String> searchFields = request.getSearchFields();
        List<HighlightField> highlightFields = (CollectionUtils.isEmpty(searchFields)
                ? List.of("content")
                : searchFields)
                .stream()
                .map(HighlightField::new)
                .toList();

        HighlightParameters params = HighlightParameters.builder()
                .withPreTags("<span style='background-color:yellow'>")
                .withPostTags("</span>")
                .build();

        HighlightQuery highlightQuery = new HighlightQuery(
                new Highlight(params, highlightFields),
                null
        );

        return NativeQuery.builder()
                .withQuery(query)
                .withPageable(pageable)
                .withHighlightQuery(highlightQuery)
                .build();
    }

    private static Query buildQuery(SearchRequest request) {
        Query textQuery = buildTextQuery(
                request.getSearchTerm(),
                request.getSearchFields()
        );

        List<Query> filters = new ArrayList<>();
        filters.addAll(buildTermFilters(request.getTermFilters()));
        filters.addAll(buildRangeFilters(request.getRangeFilters()));

        return Query.of(q -> q.bool(b -> {
            b.must(textQuery);
            if (!filters.isEmpty()) {
                b.filter(filters);
            }
            return b;
        }));
    }

    private static Query buildTextQuery(String searchTerm, List<String> fields) {
        if (!StringUtils.hasText(searchTerm)) {
            return Query.of(q -> q.matchAll(m -> m));
        }

        String field = CollectionUtils.isEmpty(fields) ? "content" : fields.get(0);

        return Query.of(q -> q.match(m -> m
                .field(field)
                .query(searchTerm)
        ));
    }

    private static List<Query> buildTermFilters(Map<String, String> termFilters) {
        List<Query> queries = new ArrayList<>();
        if (termFilters == null) return queries;

        termFilters.forEach((field, value) -> {
            if (StringUtils.hasText(value)) {
                queries.add(Query.of(q -> q.term(t -> t.field(field).value(value))));
            }
        });
        return queries;
    }

    private static List<Query> buildRangeFilters(Map<String, RangeFilter> rangeFilters) {
        List<Query> queries = new ArrayList<>();
        if (rangeFilters == null) return queries;

        rangeFilters.forEach((field, range) -> {
            if (range == null) return;
            if (!StringUtils.hasText(range.getFrom()) && !StringUtils.hasText(range.getTo())) return;

            queries.add(Query.of(q -> q.range(r -> r.untyped(u -> {
                u.field(field);
                if (StringUtils.hasText(range.getFrom())) u.gte(JsonData.of(range.getFrom()));
                if (StringUtils.hasText(range.getTo())) u.lte(JsonData.of(range.getTo()));
                return u;
            }))));
        });

        return queries;
    }
}
