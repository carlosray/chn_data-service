package ru.vas.dataservice.db.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vas.dataservice.db.domain.UpdateResource;

public interface UpdateResourceRepository extends MongoRepository<UpdateResource, String> {
}
