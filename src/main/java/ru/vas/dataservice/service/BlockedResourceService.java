package ru.vas.dataservice.service;

import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.domain.UpdateResource;

public interface BlockedResourceService {

    /**
     * Сохранить новый заблокированный ресурс
     * @param blockedResource заблокировнный ресурс
     * @return сохраненный ресурс
     */
    BlockedResource saveNew(BlockedResource blockedResource);

    /**
     * Проверка, что такой же ресурс не сохранен уже
     * @param blockedResource заблокированный ресурс
     * @return true - еще не сохранен, false - уже сохранен
     */
    boolean sameNotExists(BlockedResource blockedResource);

    /**
     * Найти такой же заблокированный ресурс и установить ему обновление
     * @param sourceBlockedResource заблокированный ресурс
     * @param updateResource обновление
     * @return обновленный заблокированный ресурс
     */
    BlockedResource findSameAndSetUpdate(BlockedResource sourceBlockedResource, UpdateResource updateResource);
}
