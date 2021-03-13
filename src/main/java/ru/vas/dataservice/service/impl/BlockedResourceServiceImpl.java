package ru.vas.dataservice.service.impl;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.stereotype.Service;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.exception.UpdateNotFoundException;
import ru.vas.dataservice.model.BlockedResourceInfo;
import ru.vas.dataservice.model.SaveInfo;
import ru.vas.dataservice.service.BlockedResourceService;
import ru.vas.dataservice.service.UpdateResourceService;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@Slf4j
public class BlockedResourceServiceImpl implements BlockedResourceService {
    private final HashOperations<String, String, String> hashOperations;
    private final UpdateResourceService updateResourceService;
    private static final String HASH_KEY = "blockedResource";

    public BlockedResourceServiceImpl(RedisTemplate<String, String> redisTemplate, UpdateResourceService updateResourceService) {
        this.hashOperations = redisTemplate.opsForHash();
        this.updateResourceService = updateResourceService;
    }

    @Override
    public SaveInfo save(List<BlockedResource> sourceBlockedResources) {
        Map<String, String> grouped = sourceBlockedResources.stream()
                .collect(Collectors.toMap(BlockedResource::getRowLine, BlockedResource::getUpdateId, (t, t2) -> t2));
        hashOperations.putAll(HASH_KEY, grouped);
        return new SaveInfo(sourceBlockedResources.size());
    }

    @Override
    public Set<BlockedResourceInfo> searchByIp(String search, boolean isActual) {
        return getSearchInfoStream(search, isActual)
                .filter(info -> info.getIp().contains(search))
                .collect(Collectors.toSet());
    }

    @Override
    public Map<String, Boolean> searchStatusByIp(Set<String> search, boolean isActual) {
        Map<String, Boolean> result = new HashMap<>();
        search.forEach(ip -> {
            result.put(ip, this.getSearchInfoStream(ip, isActual)
                    .anyMatch(info -> info.getIp().contains(ip)));
        });
        return result;
    }

    @Override
    public Set<BlockedResourceInfo> searchByDomain(String search, boolean isActual) {
        return getSearchInfoStream(search, isActual)
                .filter(info -> search.equals(info.getDomain()))
                .collect(Collectors.toSet());
    }

    private Stream<BlockedResourceInfo> getSearchInfoStream(String search, boolean isActual) {
        return getSearchRowStream(search)
                .filter(entry -> !isActual || isActualUpdate(entry.getValue()))
                .map(entry -> new BlockedResourceInfo(entry.getKey()));
    }

    @SneakyThrows
    private Stream<Map.Entry<String, String>> getSearchRowStream(String search) {
        final Cursor<Map.Entry<String, String>> scan = hashOperations
                .scan(HASH_KEY, ScanOptions.scanOptions().match(String.format("*%s*", search)).build());
        Iterable<Map.Entry<String, String>> iterable = () -> scan;
        final Set<Map.Entry<String, String>> entries = StreamSupport.stream(iterable.spliterator(), false).collect(Collectors.toSet());
        scan.close();
        return entries.stream();
    }

    private boolean isActualUpdate(String correlationId) {
        try {
            return updateResourceService.getActualUpdate().getCorrelationId().equals(correlationId);
        } catch (UpdateNotFoundException e) {
            return false;
        }
    }

    @Override
    public long count(boolean isActual) {
        return isActual ?
                hashOperations.values(HASH_KEY).parallelStream()
                        .filter(this::isActualUpdate)
                        .count() :
                hashOperations.size(HASH_KEY);
    }
}
