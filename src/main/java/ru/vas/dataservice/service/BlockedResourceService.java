package ru.vas.dataservice.service;

import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.model.BlockedResourceInfo;
import ru.vas.dataservice.model.SaveInfo;

import java.util.List;
import java.util.Set;

public interface BlockedResourceService {

    /**
     * Найти такой же заблокированный ресурс и установить ему обновление либо создать новый
     * @param sourceBlockedResources заблокированные ресурсы
     * @return статистика
     */
    SaveInfo save(List<BlockedResource> sourceBlockedResources);

    Set<BlockedResourceInfo> searchByIp(String search);

    Set<BlockedResourceInfo> searchByDomain(String search);
}
