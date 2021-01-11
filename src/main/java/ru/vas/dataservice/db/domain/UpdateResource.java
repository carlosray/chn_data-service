package ru.vas.dataservice.db.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document("updateResource")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "correlationId")
public class UpdateResource {
    @Id
    private String correlationId;
    private String fileName;
    private final LocalDateTime creationTime = LocalDateTime.now();

    public UpdateResource(String correlationId, String fileName) {
        this.correlationId = correlationId;
        this.fileName = fileName;
    }
}
