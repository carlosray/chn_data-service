package ru.vas.dataservice.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.vas.dataservice.service.BlockedResourceService;

@RestController("api")
@RequiredArgsConstructor
public class RestDataController {
    BlockedResourceService blockedResourceService;

    @GetMapping
    public ResponseEntity<Long> getSizeOfAll() {
        return ResponseEntity.ok(1L);
    }
}
