package ru.vas.dataservice.service;

import org.springframework.messaging.MessageHeaders;
import ru.vas.dataservice.db.domain.UpdateResource;

public interface UpdateResourceService {
    /**
     * Проверка что такого обновления еще не было
     * @param updateResource обновление
     * @return true - не было, false - было
     */
    boolean notExists(UpdateResource updateResource);

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
}
