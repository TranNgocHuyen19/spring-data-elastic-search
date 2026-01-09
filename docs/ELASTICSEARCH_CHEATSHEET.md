# 📚 Elasticsearch Cheat Sheet for Vietnamese Chat Search

> **Tác giả:** Trần Ngọc Huyền  
> **Cập nhật:** 2026-01-09  
> **Dự án:** Spring Data Elasticsearch - Chat Message Search

---

## 📋 Mục lục

1. [Cơ bản](#1-cơ-bản)
2. [Mapping & Settings](#2-mapping--settings)
3. [Query Types](#3-query-types)
4. [Bool Query - Kết hợp điều kiện](#4-bool-query---kết-hợp-điều-kiện)
5. [Fuzzy Search - Tìm sai chính tả](#5-fuzzy-search---tìm-sai-chính-tả)
6. [Pagination & Sorting](#6-pagination--sorting)
7. [Highlight - Đánh dấu từ khóa](#7-highlight---đánh-dấu-từ-khóa)
8. [Analyzer - Xử lý text](#8-analyzer---xử-lý-text)
9. [Vietnamese Chat Search - Final Config](#9-vietnamese-chat-search---final-config)

---

## 1. Cơ bản

### Kiểm tra Elasticsearch

```json
GET /
```

### Xem tất cả indices

```json
GET _cat/indices?v
```

### Tạo index rỗng

```json
PUT messages
```

### Thêm document

```json
POST messages/_doc/1
{
  "roomId": "ROOM_01",
  "sender": { "id": "user1", "name": "Tran Huyen" },
  "content": "Xin chào",
  "createdAt": "2026-01-08T21:30:00"
}
```

### Xem document

```json
GET messages/_doc/1
```

### Xoá index

```json
DELETE messages
```

---

## 2. Mapping & Settings

### Field Types

| Type      | Mô tả                           | Ví dụ                |
| --------- | ------------------------------- | -------------------- |
| `text`    | Full-text search, ES tự tách từ | content, description |
| `keyword` | Search chính xác, không tách    | roomId, status       |
| `date`    | Ngày tháng                      | createdAt            |
| `object`  | Nested object                   | sender { id, name }  |

### Ví dụ Mapping

```json
PUT messages
{
  "settings": {
    "number_of_shards": 1,
    "number_of_replicas": 1
  },
  "mappings": {
    "properties": {
      "id": { "type": "keyword" },
      "roomId": { "type": "keyword" },
      "content": { "type": "text" },
      "createdAt": {
        "type": "date",
        "format": "strict_date_optional_time||epoch_millis"
      },
      "sender": {
        "properties": {
          "id": { "type": "keyword" },
          "name": {
            "type": "text",
            "fields": {
              "keyword": { "type": "keyword", "ignore_above": 256 }
            }
          }
        }
      }
    }
  }
}
```

---

## 3. Query Types

### 🔍 Match Query (Full-text search)

> Phân tách từ, có xếp hạng, tìm gần đúng

```json
GET messages/_search
{
  "query": {
    "match": {
      "content": "huyền"
    }
  }
}
```

### 🎯 Term Query (Exact match)

> Search chính xác, dùng cho keyword

```json
GET messages/_search
{
  "query": {
    "term": {
      "roomId": "ROOM_01"
    }
  }
}
```

### 📅 Range Query (Khoảng giá trị)

> Tìm theo khoảng ngày, số

```json
GET messages/_search
{
  "query": {
    "range": {
      "createdAt": {
        "gte": "2026-01-01",
        "lte": "2026-12-31"
      }
    }
  }
}
```

### 📝 Match Phrase (Đúng thứ tự)

> Tìm cụm từ giữ nguyên thứ tự

```json
GET messages/_search
{
  "query": {
    "match_phrase": {
      "content": "H u y ề n"
    }
  }
}
```

### 🔤 Prefix Query (Gõ nửa chữ)

```json
GET messages/_search
{
  "query": {
    "prefix": {
      "content": "huy"
    }
  }
}
```

### 🌟 Multi Match (Tìm nhiều fields)

```json
GET messages/_search
{
  "query": {
    "multi_match": {
      "query": "huyền",
      "fields": ["content^2", "sender.name"]
    }
  }
}
```

> `^2` = boost, ưu tiên field này cao hơn

---

## 4. Bool Query - Kết hợp điều kiện

### Cấu trúc Bool Query

| Clause     | Mô tả               | Ảnh hưởng Score |
| ---------- | ------------------- | --------------- |
| `must`     | BẮT BUỘC đúng (AND) | ✅ Có           |
| `filter`   | Lọc nhanh           | ❌ Không        |
| `should`   | NÊN đúng (OR)       | ✅ Có           |
| `must_not` | KHÔNG được có       | ❌ Không        |

### Ví dụ đầy đủ

```json
GET messages/_search
{
  "query": {
    "bool": {
      "must": [
        { "match": { "content": "huyền" } }
      ],
      "filter": [
        { "term": { "roomId": "ROOM_01" } },
        { "range": { "createdAt": { "gte": "2026-01-01" } } }
      ],
      "should": [
        { "match": { "content": "gấp" } }
      ],
      "must_not": [
        { "match": { "content": "spam" } }
      ]
    }
  }
}
```

### Sơ đồ Bool Query

```
┌─────────────────────────────────────────────────────────┐
│                      BOOL QUERY                         │
├─────────────────────────────────────────────────────────┤
│  ┌─────────────┐    ┌─────────────┐    ┌─────────────┐  │
│  │    MUST     │    │   FILTER    │    │   SHOULD    │  │
│  │   (AND)     │ +  │  (Lọc, k    │ +  │   (OR)      │  │
│  │ Có score    │    │  tính score)│    │ Tăng score  │  │
│  └─────────────┘    └─────────────┘    └─────────────┘  │
│                            │                             │
│                    ┌───────▼───────┐                    │
│                    │   MUST_NOT    │                    │
│                    │  (Loại trừ)   │                    │
│                    └───────────────┘                    │
└─────────────────────────────────────────────────────────┘
```

---

## 5. Fuzzy Search - Tìm sai chính tả

### Basic Fuzzy

```json
GET messages/_search
{
  "query": {
    "match": {
      "content": {
        "query": "huyen",
        "fuzziness": "AUTO"
      }
    }
  }
}
```

### Fuzziness Levels

| Giá trị | Mô tả                  |
| ------- | ---------------------- |
| `0`     | Chính xác              |
| `1`     | Cho phép 1 ký tự sai   |
| `2`     | Cho phép 2 ký tự sai   |
| `AUTO`  | Tự động theo độ dài từ |

### AUTO Fuzziness Rules

```
Độ dài từ 0-2  → 0 (chính xác)
Độ dài từ 3-5  → 1 ký tự sai
Độ dài từ > 5  → 2 ký tự sai
```

---

## 6. Pagination & Sorting

### Phân trang

```json
GET messages/_search
{
  "from": 0,      // offset (page * size)
  "size": 10,     // số record mỗi trang
  "query": {
    "match": { "content": "huyền" }
  }
}
```

### Sắp xếp

```json
GET messages/_search
{
  "query": {
    "match": { "content": "huyền" }
  },
  "sort": [
    { "createdAt": "desc" },
    { "_score": "desc" }
  ]
}
```

---

## 7. Highlight - Đánh dấu từ khóa

```json
GET messages/_search
{
  "query": {
    "match": { "content": "huyền" }
  },
  "highlight": {
    "pre_tags": ["<span style='background-color:yellow'>"],
    "post_tags": ["</span>"],
    "fields": {
      "content": {}
    }
  }
}
```

### Kết quả

```json
{
  "highlight": {
    "content": ["Xin chào <span style='background-color:yellow'>Huyền</span>!"]
  }
}
```

---

## 8. Analyzer - Xử lý text

### Analyzer Flow

```
┌─────────────┐    ┌─────────────┐    ┌─────────────┐
│  TOKENIZER  │ →  │   FILTER    │ →  │   TOKENS    │
│  (Tách từ)  │    │ (Xử lý từ)  │    │  (Kết quả)  │
└─────────────┘    └─────────────┘    └─────────────┘

Ví dụ: "Xin Chào HUYỀN"
       ↓ standard tokenizer
       ["Xin", "Chào", "HUYỀN"]
       ↓ lowercase filter
       ["xin", "chào", "huyền"]
       ↓ asciifolding filter
       ["xin", "chao", "huyen"]
```

### Các Filter phổ biến

| Filter         | Mô tả         | Ví dụ                          |
| -------------- | ------------- | ------------------------------ |
| `lowercase`    | Chuyển thường | HUYỀN → huyền                  |
| `asciifolding` | Bỏ dấu        | huyền → huyen                  |
| `edge_ngram`   | Tách prefix   | huyen → [hu, huy, huye, huyen] |
| `synonym`      | Từ đồng nghĩa | huyen = huyền                  |

### Test Analyzer

```json
GET messages/_analyze
{
  "analyzer": "my_analyzer",
  "text": "Xin chào Huyền"
}
```

---

## 9. Vietnamese Chat Search - Final Config

### 🎯 Mục tiêu

- Tìm được: `Huyền`, `Huyen`, `Huỳen`, `Huyển`, `Huyeeenf`, `Huyềnn`...
- Sort theo thời gian mới nhất
- Highlight từ khóa
- Filter theo roomId, date range

### Settings & Mappings

```json
PUT message_index
{
  "settings": {
    "analysis": {
      "filter": {
        "vn_fold": {
          "type": "asciifolding",
          "preserve_original": true    // Giữ cả bản gốc
        },
        "ngram_filter": {
          "type": "edge_ngram",
          "min_gram": 2,
          "max_gram": 20
        }
      },
      "analyzer": {
        "vn_chat_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "vn_fold", "ngram_filter"]
        },
        "vn_search_analyzer": {
          "type": "custom",
          "tokenizer": "standard",
          "filter": ["lowercase", "vn_fold"]
        }
      }
    }
  },
  "mappings": {
    "properties": {
      "content": {
        "type": "text",
        "analyzer": "vn_chat_analyzer",        // Index time
        "search_analyzer": "vn_search_analyzer" // Search time
      },
      "roomId": { "type": "keyword" },
      "createdAt": {
        "type": "date",
        "format": "yyyy-MM-dd'T'HH:mm:ss.SSS||yyyy-MM-dd'T'HH:mm:ss||yyyy-MM-dd||epoch_millis"
      }
    }
  }
}
```

### Luồng xử lý Analyzer

```
📝 INDEX TIME (khi lưu document)
─────────────────────────────────────────────────
"Huyền đẹp gái"
    ↓ standard tokenizer
["Huyền", "đẹp", "gái"]
    ↓ lowercase
["huyền", "đẹp", "gái"]
    ↓ vn_fold (asciifolding + preserve_original)
["huyền", "huyen", "đẹp", "dep", "gái", "gai"]
    ↓ edge_ngram (min=2, max=20)
["hu", "huy", "huye", "huyen", "huyền", ...]

🔍 SEARCH TIME (khi tìm kiếm)
─────────────────────────────────────────────────
"huyen"
    ↓ standard tokenizer
["huyen"]
    ↓ lowercase
["huyen"]
    ↓ vn_fold
["huyen"]

✅ MATCH! "huyen" có trong tokens đã index
```

### Final Query

```json
GET message_index/_search
{
  "size": 20,
  "sort": [
    { "createdAt": { "order": "desc" } }
  ],
  "query": {
    "bool": {
      "must": [
        {
          "match": {
            "content": {
              "query": "Huyen"
            }
          }
        }
      ],
      "filter": [
        { "term": { "roomId": "room_1" } },
        {
          "range": {
            "createdAt": {
              "gte": "2026-01-01T08:00:00",
              "lte": "2026-01-01T23:59:00"
            }
          }
        }
      ]
    }
  },
  "highlight": {
    "pre_tags": ["<span style='background-color:yellow'>"],
    "post_tags": ["</span>"],
    "fields": {
      "content": {}
    }
  }
}
```

### Test Cases

| Input      | Tìm thấy | Lý do             |
| ---------- | -------- | ----------------- |
| `Huyền`    | ✅       | Exact match       |
| `Huyen`    | ✅       | asciifolding      |
| `huyen`    | ✅       | lowercase         |
| `Huỳen`    | ✅       | edge_ngram prefix |
| `Huyển`    | ✅       | edge_ngram prefix |
| `Huyeeenf` | ✅       | edge_ngram prefix |
| `Huyềnn`   | ✅       | edge_ngram prefix |
| `hu`       | ✅       | edge_ngram min=2  |
| `huy`      | ✅       | edge_ngram        |

---

## 📌 Quick Reference

### Bulk Insert

```json
POST message_index/_bulk
{ "index": { "_id": "1" } }
{ "roomId": "room_1", "content": "Huyền ơi", "createdAt": "2026-01-01T08:00:00.000" }
{ "index": { "_id": "2" } }
{ "roomId": "room_1", "content": "Huyen check tin", "createdAt": "2026-01-01T09:00:00.000" }
```

### Java Spring Data Elasticsearch

```java
// Simple match query
return Query.of(q -> q.match(m -> m
        .field("content")
        .query(searchTerm)
));

// Bool query với filter
return Query.of(q -> q.bool(b -> {
    b.must(textQuery);
    b.filter(filters);
    return b;
}));

// Highlight
HighlightParameters params = HighlightParameters.builder()
        .withPreTags("<span style='background-color:yellow'>")
        .withPostTags("</span>")
        .build();
```

---

## 🔗 Resources

- [Elasticsearch Official Docs](https://www.elastic.co/guide/en/elasticsearch/reference/current/index.html)
- [Spring Data Elasticsearch](https://docs.spring.io/spring-data/elasticsearch/docs/current/reference/html/)
- [Vietnamese Text Processing](https://www.elastic.co/guide/en/elasticsearch/plugins/current/analysis-icu.html)

---

> 💡 **Tip:** Luôn test analyzer trước khi deploy với `_analyze` API!
