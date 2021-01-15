package ru.vas.dataservice.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
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
@With
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"rowLine"})
public class BlockedResource {
    @Id
    @Indexed
    private String rowLine;
    private List<String> ip;
    private String domain;
    private String reason;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBlock;
    private Set<String> additionalParams = new HashSet<>();
    @DBRef(lazy = true)
    private UpdateResource update;
}
