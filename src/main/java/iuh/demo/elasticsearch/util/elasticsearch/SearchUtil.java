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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@NoArgsConstructor
public final class SearchUtil {

    private static final String DEFAULT_SORT_FIELD = "createdAt";
    private static final DateTimeFormatter ES_DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS");

    public static NativeQuery buildNativeQuery(SearchRequest request) {
        Query textQuery = buildTextQuery(request);
        Query rangeQuery = buildRangeQuery(request);

        if (textQuery == null && rangeQuery == null) {
            return null;
        }

        Query finalQuery = combineQueries(textQuery, rangeQuery);

        Pageable pageable = PageRequest.of(
                Math.max(request.getPage(), 0),
                Math.max(request.getSize(), 10),
                buildSort(request)
        );

        return NativeQuery.builder()
                .withQuery(finalQuery)
                .withPageable(pageable)
                .build();
    }

    private static Query buildTextQuery(SearchRequest request) {
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
                    .operator(Operator.And)
            ));
        }

        return Query.of(q -> q.multiMatch(m -> m
                .query(request.getSearchTerm())
                .fields(fields)
                .type(TextQueryType.CrossFields)
                .operator(Operator.And)
        ));
    }

    private static Query buildRangeQuery(SearchRequest request) {
        LocalDate from = request.getFromDate();
        LocalDate to = request.getToDate();

        if (from == null && to == null) {
            return null;
        }

        return Query.of(q -> q.range(r -> r
                .date(d -> {
                    d.field("createdAt");

                    if (from != null) {
                        LocalDateTime fromDateTime = from.atStartOfDay();
                        d.gte(fromDateTime.format(ES_DATE_FORMATTER));
                    }
                    if (to != null) {
                        LocalDateTime toDateTime = to.atTime(23, 59, 59, 999000000);
                        d.lte(toDateTime.format(ES_DATE_FORMATTER));
                    }
                    return d;
                })
        ));
    }

    private static Query combineQueries(Query textQuery, Query rangeQuery) {
        if (textQuery != null && rangeQuery != null) {
            return Query.of(q -> q.bool(b -> b
                    .must(textQuery)
                    .filter(rangeQuery)
            ));
        }
        return textQuery != null ? textQuery : rangeQuery;
    }

    private static Sort buildSort(SearchRequest request) {
        String sortBy = StringUtils.hasText(request.getSortBy())
                ? request.getSortBy()
                : DEFAULT_SORT_FIELD;

        Sort.Direction direction = "ASC".equalsIgnoreCase(request.getSortOrder())
                ? Sort.Direction.ASC
                : Sort.Direction.DESC;

        return Sort.by(direction, sortBy);
    }
}