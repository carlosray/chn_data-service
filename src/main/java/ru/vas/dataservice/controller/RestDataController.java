package ru.vas.dataservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.vas.dataservice.db.domain.UpdateResource;
import ru.vas.dataservice.model.BlockedResourceInfo;
import ru.vas.dataservice.service.BlockedResourceService;
import ru.vas.dataservice.service.UpdateResourceService;

import java.util.Set;

@RestController
@RequestMapping("api")
@RequiredArgsConstructor
public class RestDataController {
    private final BlockedResourceService blockedResourceService;
    private final UpdateResourceService updateResourceService;

    @GetMapping("blocked/search/ip")
    public ResponseEntity<Set<BlockedResourceInfo>> searchId(@RequestParam String search,
                                                             @RequestParam(required = false, defaultValue = "true") boolean actual) {
        return ResponseEntity.ok(blockedResourceService.searchByIp(search, actual));
    }

    @GetMapping("blocked/search/domain")
    public ResponseEntity<Set<BlockedResourceInfo>> searchDomain(@RequestParam String search,
                                                                 @RequestParam(required = false, defaultValue = "true") boolean actual) {
        return ResponseEntity.ok(blockedResourceService.searchByDomain(search, actual));
    }

    @GetMapping("blocked/count")
    public ResponseEntity<Long> searchCountBlocked(@RequestParam(required = false, defaultValue = "true") boolean actual) {
        return ResponseEntity.ok(blockedResourceService.count(actual));
    }

    @GetMapping("update/count")
    public ResponseEntity<Long> searchCountUpdates() {
        return ResponseEntity.ok(updateResourceService.countOfUpdates());
    }

    @GetMapping("update")
    public ResponseEntity<Iterable<UpdateResource>> getAllUpdates() {
        return ResponseEntity.ok(updateResourceService.findAll());
    }

}
