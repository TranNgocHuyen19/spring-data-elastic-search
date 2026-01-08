package iuh.demo.elasticsearch.model.elasticsearch;

import iuh.demo.elasticsearch.util.elasticsearch.Indices;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Mapping;
import org.springframework.data.elasticsearch.annotations.Setting;

@Document(indexName = Indices.USER_INDEX)
@Setting(settingPath = "/static/elasticsearch/es-settings.json")
@Mapping(mappingPath = "/static/elasticsearch/mappings/user.json")
@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class UserDoc {

    private String id;

    private String name;

}
