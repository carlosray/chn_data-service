package ru.vas.dataservice.db.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.vas.dataservice.db.domain.UpdateResource;

import java.util.Optional;

@Repository
public interface UpdateResourceRepository extends ElasticsearchRepository<UpdateResource, String> {
    Optional<UpdateResource> findTopByOrderByCreationTimeDesc();
}
