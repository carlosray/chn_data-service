package ru.vas.dataservice.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;
import lombok.ToString;
import ru.vas.dataservice.db.domain.BlockedResource;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Getter
@ToString
public class BlockedResourceInfo {
    private final List<String> ip;
    private final String domain;
    private final String reason;
    @JsonFormat(pattern = "yyyy-MM-dd", shape = JsonFormat.Shape.STRING)
    private final LocalDate dateOfBlock;
    private final Set<String> additionalParams;

    public BlockedResourceInfo(BlockedResource blockedResource) {
        this.ip = blockedResource.getIp();
        this.domain = blockedResource.getDomain();
        this.reason = blockedResource.getReason();
        this.dateOfBlock = blockedResource.getDateOfBlock();
        this.additionalParams = blockedResource.getAdditionalParams();
    }

    public static Set<BlockedResourceInfo> convertToDTO(Set<BlockedResource> blockedResources) {
        return blockedResources.stream()
                .map(BlockedResourceInfo::new)
                .collect(Collectors.toSet());
    }
}
