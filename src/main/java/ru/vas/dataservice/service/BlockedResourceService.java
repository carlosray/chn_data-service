package ru.vas.dataservice.service;

import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.model.SaveInfo;

import java.util.List;

public interface BlockedResourceService {

    /**
     * Сохранить новые заблокированные ресурсы
     * @param blockedResource заблокировнные ресурсы
     * @return сохраненный ресурс
     */
    List<BlockedResource> saveNew(List<BlockedResource> blockedResource);

    /**
     * Проверка, что такой же ресурс не сохранен уже
     * @param blockedResource заблокированный ресурс
     * @return true - еще не сохранен, false - уже сохранен
     */
    boolean sameNotExists(BlockedResource blockedResource);

    /**
     * Найти такой же заблокированный ресурс и установить ему обновление либо создать новый
     * @param sourceBlockedResources заблокированные ресурсы
     * @return статистика
     */
    SaveInfo findSameAndSetUpdate(List<BlockedResource> sourceBlockedResources);
}
