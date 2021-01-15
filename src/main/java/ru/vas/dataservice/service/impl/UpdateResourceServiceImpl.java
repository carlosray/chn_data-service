package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.db.repo.UpdateResourceRepository;
import ru.vas.dataservice.service.UpdateResourceService;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

@Service
@RequiredArgsConstructor
public class UpdateResourceServiceImpl implements UpdateResourceService {
    private final UpdateResourceRepository updateResourceRepository;
    private static final Set<String> correlationIdCache = ConcurrentHashMap.newKeySet();


    @Override
    public synchronized boolean exists(UpdateResource updateResource) {
        return isCached(updateResource) || isExistsInDB(updateResource);
    }

    private boolean isCached(UpdateResource updateResource) {
        return correlationIdCache.contains(updateResource.getCorrelationId());
    }

    private boolean isExistsInDB(UpdateResource updateResource) {
        return updateResourceRepository.existsById(updateResource.getCorrelationId());
    }

    @Override
    public UpdateResource saveNew(UpdateResource updateResource) {
        final UpdateResource saved = updateResourceRepository.save(updateResource);
        correlationIdCache.add(saved.getCorrelationId());
        return saved;
    }

    @Override
    public UpdateResource getUpdateInfo(MessageHeaders headers) {
        return new UpdateResource(
                headers.getOrDefault(IntegrationMessageHeaderAccessor.CORRELATION_ID, "unknown_correlation_id").toString(),
                headers.getOrDefault("file_name", "unknown_file_name").toString());
    }
}
