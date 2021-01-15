package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.repo.BlockedResourceRepository;
import ru.vas.dataservice.model.SaveInfo;
import ru.vas.dataservice.service.BlockedResourceService;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@RequiredArgsConstructor
@Slf4j
public class BlockedResourceServiceImpl implements BlockedResourceService {
    private final BlockedResourceRepository blockedResourceRepository;

    @Override
    public List<BlockedResource> saveNew(List<BlockedResource> blockedResources) {
        final List<BlockedResource> blockedResourcess = blockedResourceRepository.saveAll(blockedResources);
        log.info("Сохранено " + blockedResourcess.size() + " ресурсов");
        return blockedResources;
    }

    @Override
    public boolean sameNotExists(BlockedResource blockedResource) {
        return !blockedResourceRepository.existsById(blockedResource.getRowLine());
    }

    @Transactional
    @Override
    public SaveInfo findSameAndSetUpdate(List<BlockedResource> sourceBlockedResources) {
        AtomicInteger updated = new AtomicInteger();
        AtomicInteger created = new AtomicInteger();
        sourceBlockedResources.parallelStream()
                .forEach(source -> blockedResourceRepository.findById(source.getRowLine())
                        .map(resource -> {
                            resource.setUpdate(source.getUpdate());
                            return resource;
                        })
                        .map(blockedResourceRepository::save)
                        .map(resource -> {
                            updated.getAndIncrement();
                            return resource;
                        })
                        .orElseGet(() -> {
                            created.getAndIncrement();
                            return blockedResourceRepository.save(source);
                        }));
        return new SaveInfo(updated.get(), created.get());
    }

}
