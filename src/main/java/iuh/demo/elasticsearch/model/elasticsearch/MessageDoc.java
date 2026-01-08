package iuh.demo.elasticsearch.model.elasticsearch;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iuh.demo.elasticsearch.model.common.UserInfo;
import iuh.demo.elasticsearch.util.elasticsearch.Indices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.*;

import java.time.LocalDateTime;

@org.springframework.data.mongodb.core.mapping.Document(collection = "messages")
@Document(indexName = Indices.MESSAGE_INDEX)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "static/elasticsearch/es-settings.json")
@Builder
public class MessageDoc {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String roomId;

    @Field(type = FieldType.Object)
    private UserInfo sender;

    @Field(type = FieldType.Text)
    private String content;

    @Field(type = FieldType.Date, format = DateFormat.date_hour_minute_second_millis)
    private LocalDateTime createdAt;
}
