package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.db.repo.BlockedResourceRepository;
import ru.vas.dataservice.service.BlockedResourceService;

@Service
@RequiredArgsConstructor
public class BlockedResourceServiceImpl implements BlockedResourceService {
    private final BlockedResourceRepository blockedResourceRepository;

    @Override
    public BlockedResource saveNew(BlockedResource blockedResource) {
        return blockedResourceRepository.save(blockedResource);
    }

    @Override
    public boolean sameNotExists(BlockedResource blockedResource) {
        return !blockedResourceRepository.exists(getFullExample(blockedResource));
    }

    private Example<BlockedResource> getFullExample(BlockedResource blockedResource) {
        return Example.of(blockedResource, ExampleMatcher
                .matchingAll()
                .withIgnorePaths("id", "update"));
    }

    @Override
    public BlockedResource findSameAndSetUpdate(BlockedResource sourceBlockedResource, UpdateResource updateResource) {
        return blockedResourceRepository.findAll(getFullExample(sourceBlockedResource))
                .stream()
                .peek(resource -> resource.setUpdate(updateResource))
                .map(blockedResourceRepository::save)
                .findFirst()
                .orElse(sourceBlockedResource);
    }

}
