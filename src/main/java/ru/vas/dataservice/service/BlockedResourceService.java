package ru.vas.dataservice.service;

import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.model.BlockedResourceInfo;
import ru.vas.dataservice.model.SaveInfo;

import java.util.List;
import java.util.Map;
import java.util.Set;

public interface BlockedResourceService {

    /**
     * Найти такой же заблокированный ресурс и установить ему обновление либо создать новый
     * @param sourceBlockedResources заблокированные ресурсы
     * @return статистика
     */
    SaveInfo save(List<BlockedResource> sourceBlockedResources);

    /**
     * Поиск по IP адресу
     * @param search IP адрес для поиска
     * @param isActual искать только последнее обновление
     * @return инфо по заблокированному ресурсу
     */
    Set<BlockedResourceInfo> searchByIp(String search, boolean isActual);

    /**
     * Поиск статуса по IP
     * @param search поисковые IP
     * @param isActual искать только последнее обновление
     * @return мапа IP : статус
     */
    Map<String, Boolean> searchStatusByIp(Set<String> search, boolean isActual);

    /**
     * Поиск по домену
     * @param search домен для поиска
     * @param isActual искать только последнее обновление
     * @return инфо по заблокированному ресурсу
     */
    Set<BlockedResourceInfo> searchByDomain(String search, boolean isActual);

    /**
     * Кол-во заблокированных ресурсов
     * @param isActual искать только последнее обновление
     * @return кол-во
     */
    long count(boolean isActual);
}
