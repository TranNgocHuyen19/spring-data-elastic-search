package iuh.demo.elasticsearch.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import iuh.demo.elasticsearch.util.Indices;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;
import org.springframework.data.elasticsearch.annotations.Setting;

@org.springframework.data.mongodb.core.mapping.Document(collection = "messages")
@Document(indexName = Indices.MESSAGE_INDEX)
@NoArgsConstructor
@AllArgsConstructor
@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@Setting(settingPath = "static/elasticsearch/es-settings.json")
public class Message {

    @Id
    private String id;

    @Field(type = FieldType.Keyword)
    private String roomId;

    @Field(type = FieldType.Keyword)
    private String senderId;

    @Field(type = FieldType.Text)
    private String content;

}
