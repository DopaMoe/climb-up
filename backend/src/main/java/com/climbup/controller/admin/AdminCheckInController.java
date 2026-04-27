package com.climbup.controller.admin;

import com.climbup.dto.CheckInRequest;
import com.climbup.model.CheckIn;
import com.climbup.service.CheckInService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/check-in")
@RequiredArgsConstructor
public class AdminCheckInController {

    private final CheckInService service;

    @PostMapping
    public CheckIn checkIn(@Valid @RequestBody CheckInRequest req) {
        return service.checkIn(req.userId());
    }
}
