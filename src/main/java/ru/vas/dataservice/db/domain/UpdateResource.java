package ru.vas.dataservice.db.domain;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.redis.core.RedisHash;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "correlationId")
@RedisHash("updateResource")
public class UpdateResource {
    @Id
    private String correlationId;
    private String fileName;
    private LocalDateTime creationTime = LocalDateTime.now();

    public UpdateResource(String correlationId, String fileName) {
        this.correlationId = correlationId;
        this.fileName = fileName;
    }
}
