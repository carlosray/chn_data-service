package ru.vas.dataservice.db.repo;

import org.springframework.data.elasticsearch.repository.ElasticsearchRepository;
import org.springframework.stereotype.Repository;
import ru.vas.dataservice.db.domain.BlockedResource;

import java.util.Set;

@Repository
public interface BlockedResourceRepository extends ElasticsearchRepository<BlockedResource, String> {
    Set<BlockedResource> findAllByDomainIsAndUpdateIdIs(String domain, String updateId);
    Set<BlockedResource> findAllByIpContainsAndUpdateIdIs(String ip, String updateId);

    Set<BlockedResource> findAllByDomain(String domain);
    Set<BlockedResource> findAllByIpContains(String ip);

    long countByUpdateId(String updateId);

}
