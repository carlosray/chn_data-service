package ru.vas.dataservice.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.vas.dataservice.db.domain.BlockedResource;
import ru.vas.dataservice.db.repo.BlockedResourceRepository;
import ru.vas.dataservice.model.CheckStatusDTO;
import ru.vas.dataservice.model.SaveInfo;
import ru.vas.dataservice.service.BlockedResourceService;
import ru.vas.dataservice.service.UpdateResourceService;

import java.util.List;
import java.util.Set;

@Service
@Slf4j
@RequiredArgsConstructor
public class BlockedResourceServiceImpl implements BlockedResourceService {
    private final UpdateResourceService updateResourceService;
    private final BlockedResourceRepository blockedResourceRepository;

    @Transactional
    @Override
    public SaveInfo save(List<BlockedResource> sourceBlockedResources) {
        blockedResourceRepository.saveAll(sourceBlockedResources);
        return new SaveInfo(sourceBlockedResources.size());
    }

    @SneakyThrows
    @Override
    public Set<BlockedResource> searchByIp(String search, boolean isActual) {
        return isActual ?
                blockedResourceRepository.findAllByIpContainsAndUpdateIdIs(search, updateResourceService.getActualUpdate().getCorrelationId()) :
                blockedResourceRepository.findAllByIpContains(search);
    }

    @Transactional
    @Override
    public Set<CheckStatusDTO> searchStatuses(Set<CheckStatusDTO> search, boolean isActual) {
        search.parallelStream()
                .forEach(checkStatusDTO -> checkStatusDTO.setStatus(this.getStatusByType(checkStatusDTO, isActual)));
        return search;
    }

    private boolean getStatusByType(CheckStatusDTO checkStatusDTO, boolean isActual) {
        switch (checkStatusDTO.getType()) {
            case IP: return !this.searchByIp(checkStatusDTO.getValue(), isActual).isEmpty();
            case DOMAIN: return !this.searchByDomain(checkStatusDTO.getValue(), isActual).isEmpty();
            default: return false;
        }
    }

    @SneakyThrows
    @Override
    public Set<BlockedResource> searchByDomain(String search, boolean isActual) {
        return isActual ?
                blockedResourceRepository.findAllByDomainIsAndUpdateIdIs(search, updateResourceService.getActualUpdate().getCorrelationId()) :
                blockedResourceRepository.findAllByDomain(search);
    }

    @SneakyThrows
    @Override
    public long count(boolean isActual) {
        return isActual ?
                blockedResourceRepository.countByUpdateId(updateResourceService.getActualUpdate().getCorrelationId()) :
                blockedResourceRepository.count();
    }
}
