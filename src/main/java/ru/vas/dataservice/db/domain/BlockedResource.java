package ru.vas.dataservice.db.domain;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
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
    private LocalDate dateOfBlock;
    private Set<String> additionalParams = new HashSet<>();
}
