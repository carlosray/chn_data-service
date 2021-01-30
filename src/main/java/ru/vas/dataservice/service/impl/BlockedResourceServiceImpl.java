package ru.vas.dataservice.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.model.BlockedResourceInfo;
import ru.vas.dataservice.model.SaveInfo;
import ru.vas.dataservice.service.BlockedResourceService;

import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Slf4j
public class BlockedResourceServiceImpl implements BlockedResourceService {
    private final HashOperations<String, String, String> hashOperations;
    private static final String HASH_KEY = "blockedResource";

    public BlockedResourceServiceImpl(RedisTemplate<String, String> redisTemplate) {
        this.hashOperations = redisTemplate.opsForHash();
    }

    @Override
    public SaveInfo save(List<BlockedResource> sourceBlockedResources) {
        Map<String, String> grouped = sourceBlockedResources.stream()
                .collect(Collectors.toMap(BlockedResource::getRowLine, BlockedResource::getUpdateId, (t, t2) -> t2));
        hashOperations.putAll(HASH_KEY, grouped);
        return new SaveInfo(sourceBlockedResources.size());
    }

    @Override
    public Set<BlockedResourceInfo> searchByIp(String search) {
        return null;
    }

    @Override
    public Set<BlockedResourceInfo> searchByDomain(String search) {
        return null;
    }
}
