package ru.vas.dataservice.db.repo;

import org.springframework.data.mongodb.repository.MongoRepository;
import ru.vas.dataservice.db.domain.BlockedResource;

public interface BlockedResourceRepository extends MongoRepository<BlockedResource, String> {
    boolean existsByRowLine(String rowLine);
}
