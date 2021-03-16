package ru.vas.dataservice.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(of = "correlationId")
@Document(indexName = "update-resource", createIndex = true)
public class UpdateResource {
    @Id
    private String correlationId;
    private String fileName;
    @Field(type = FieldType.Date, format = DateFormat.date_time)
    @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss'Z'", shape = JsonFormat.Shape.STRING)
    private LocalDateTime creationTime = LocalDateTime.now();

    public UpdateResource(String correlationId, String fileName) {
        this.correlationId = correlationId;
        this.fileName = fileName;
    }
}
