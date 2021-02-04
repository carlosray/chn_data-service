package ru.vas.dataservice.service;

import org.springframework.messaging.MessageHeaders;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.exception.UpdateNotFoundException;

import java.util.List;

public interface UpdateResourceService {
    /**
     * Проверка было ли уже обновление
     * @param updateResource обновление
     * @return true - было, false - не было
     */
    boolean exists(UpdateResource updateResource);

    /**
     * Сохранить обновление
     * @param updateResource обновление
     * @return сохраненное обновление
     */
    UpdateResource saveNew(UpdateResource updateResource);

    /**
     * Получить обновление из хидеров сообщения
     * @param headers хидеры
     * @return обновление
     */
    UpdateResource getUpdateInfo(MessageHeaders headers);

    /**
     * Получить актуальное (последнее) обновление
     * @return обновление
     */
    UpdateResource getActualUpdate() throws UpdateNotFoundException;

    /**
     * Получить кол-во обновлений
     * @return кол-во обновлений
     */
    long countOfUpdates();

    /**
     * Найти все обновления
     */
    Iterable<UpdateResource> findAll();
}
