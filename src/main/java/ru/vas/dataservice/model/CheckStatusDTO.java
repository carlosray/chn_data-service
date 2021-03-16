package ru.vas.dataservice.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode
public class CheckStatusDTO {
    private String value;
    private Type type;
    private Boolean status;

    public enum Type {
        IP,
        DOMAIN
    }
}
