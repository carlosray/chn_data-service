package ru.vas.dataservice.db.repo;

import org.springframework.data.repository.CrudRepository;
import ru.vas.dataservice.db.domain.UpdateResource;

import java.util.Optional;

public interface UpdateResourceRepository extends CrudRepository<UpdateResource, String> {
    Optional<UpdateResource> findTopByOrderByCreationTimeDesc();
}
