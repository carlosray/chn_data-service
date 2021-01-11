package ru.vas.dataservice.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Document("blockedResource")
@Data
@NoArgsConstructor
@EqualsAndHashCode(of = "id")
public class BlockedResource {
    @Id
    private String id;
    @Indexed
    private List<String> ip;
    @Indexed
    private String domain;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBlock;
    private Set<String> additionalParams = new HashSet<>();
    @DBRef(lazy = true)
    private UpdateResource update;
}
