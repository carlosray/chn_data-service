package ru.vas.dataservice.db.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.elasticsearch.annotations.DateFormat;
import org.springframework.data.elasticsearch.annotations.Document;
import org.springframework.data.elasticsearch.annotations.Field;
import org.springframework.data.elasticsearch.annotations.FieldType;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@EqualsAndHashCode(of = {"rowLine"})
@Document(indexName = "blocked-resource", createIndex = true)
public class BlockedResource {
    @Id
    private String rowLine;
    @Field(type = FieldType.Text)
    private String updateId;
    @Field(type = FieldType.Text)
    private List<String> ip;
    @Field(type = FieldType.Text)
    private String domain;
    private String reason;
    @Field(type = FieldType.Date, format = DateFormat.date, pattern = "yyyy-MM-dd")
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private LocalDate dateOfBlock;
    private final Set<String> additionalParams = new HashSet<>();

    public BlockedResource(String line, String updateId) {
        this.rowLine = line;
        this.updateId = updateId;
        setUp(line);
    }

    private void setUp(String line) {
        final String[] params = line.split(Delimiters.SEMICOLON.getValue());
        setIp(params[0]);
        setDomain(params[1]);
        addAdditionalParam(params[2]);
        setReason(params[3]);
        setNumber(params[4]);
        setDateOfBlock(params[5]);
    }

    private void setIp(String param) {
        this.ip = Arrays.stream(param.split(Delimiters.VERT_LINE.getValue()))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    private void setDomain(String param) {
        this.domain = param.trim();
    }

    private void addAdditionalParam(String param) {
        final String trimmedParam = param.trim();
        if (StringUtils.isNotBlank(trimmedParam)) {
            additionalParams.add(trimmedParam);
        }
    }

    private void setReason(String param) {
        this.reason = param.trim();
    }

    private void setNumber(String param) {
        additionalParams.add(param.trim());
    }

    private void setDateOfBlock(String param) {
        try {
            this.dateOfBlock = LocalDate.parse(param);
        } catch (DateTimeParseException ex) {
            addAdditionalParam(param);
        }
    }

    @Getter
    public enum Delimiters {
        SEMICOLON(";"),
        VERT_LINE("\\|");

        private String value;

        Delimiters(String delimiter) {
            this.value = delimiter;
        }
    }
}
