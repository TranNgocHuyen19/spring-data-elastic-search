package iuh.demo.elasticsearch.repository.mongo;

import iuh.demo.elasticsearch.model.mongodb.User;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface UserRepository extends MongoRepository<User, String> {
}
