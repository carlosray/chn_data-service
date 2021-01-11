package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.integration.IntegrationMessageHeaderAccessor;
import org.springframework.messaging.MessageHeaders;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.db.repo.UpdateResourceRepository;
import ru.vas.dataservice.service.UpdateResourceService;

@Service
@RequiredArgsConstructor
public class UpdateResourceServiceImpl implements UpdateResourceService {
    private final UpdateResourceRepository updateResourceRepository;


    @Override
    public boolean notExists(UpdateResource updateResource) {
        return !updateResourceRepository.existsById(updateResource.getCorrelationId());
    }

    @Override
    public UpdateResource saveNew(UpdateResource updateResource) {
        return updateResourceRepository.save(updateResource);
    }

    @Override
    public UpdateResource getUpdateInfo(MessageHeaders headers) {
        return new UpdateResource(
                headers.getOrDefault(IntegrationMessageHeaderAccessor.CORRELATION_ID, "unknown_correlation_id").toString(),
                headers.getOrDefault("file_name", "unknown_file_name").toString());
    }

}
