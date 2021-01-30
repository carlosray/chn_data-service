package ru.vas.dataservice.db.domain;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(of = {"rowLine"})
public class BlockedResource {
    private static final String HASH = "blockedResource";
    private String rowLine;
    private String updateId;
}
