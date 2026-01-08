package iuh.demo.elasticsearch.model;

import iuh.demo.elasticsearch.util.Indices;
import lombok.AllArgsConstructor;
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
public class User {

    private String id;

    private String name;

}
