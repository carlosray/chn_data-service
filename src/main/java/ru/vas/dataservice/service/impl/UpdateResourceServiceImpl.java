package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.db.repo.UpdateResourceRepository;
import ru.vas.dataservice.exception.UpdateNotFoundException;
import ru.vas.dataservice.service.UpdateResourceService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UpdateResourceServiceImpl implements UpdateResourceService {
    private final UpdateResourceRepository updateResourceRepository;
    private static final Set<String> correlationIdCache = ConcurrentHashMap.newKeySet();


    @Override
    public boolean exists(UpdateResource updateResource) {
        return isCached(updateResource) || isExistsInDB(updateResource);
    }

    private boolean isCached(UpdateResource updateResource) {
        return correlationIdCache.contains(updateResource.getCorrelationId());
    }

    private boolean isExistsInDB(UpdateResource updateResource) {
        final boolean existsById = updateResourceRepository.existsById(updateResource.getCorrelationId());
        if (existsById) correlationIdCache.add(updateResource.getCorrelationId());
        return existsById;
    }

    @Override
    @CacheEvict(value = {"updates", "updateInfo"}, allEntries = true)
    public UpdateResource saveNew(UpdateResource updateResource) {
        final UpdateResource saved = updateResourceRepository.save(updateResource);
        correlationIdCache.add(saved.getCorrelationId());
        return saved;
    }

    @Override
    @Cacheable("updateInfo")
    public UpdateResource getUpdateInfo(MessageHeaders headers) {
        return new UpdateResource(
                headers.getOrDefault(IntegrationMessageHeaderAccessor.CORRELATION_ID, "unknown_correlation_id").toString(),
                headers.getOrDefault("file_name", "unknown_file_name").toString());
    }

    @Override
    @Cacheable("updates")
    public UpdateResource getActualUpdate() throws UpdateNotFoundException {
        return updateResourceRepository.findTopByOrderByCreationTimeDesc()
                .orElseThrow(() -> new UpdateNotFoundException("Обновлений не найдено"));
    }

    @Override
    public long countOfUpdates() {
        return updateResourceRepository.count();
    }

    @Override
    public Iterable<UpdateResource> findAll() {
        return updateResourceRepository.findAll();
    }
}
