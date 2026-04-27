package com.climbup.controller.admin;

import com.climbup.dto.MembershipTypeRequest;
import com.climbup.model.MembershipType;
import com.climbup.service.MembershipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin/membership-types")
@RequiredArgsConstructor
public class AdminMembershipTypeController {

    private final MembershipTypeService service;

    @GetMapping
    public List<MembershipType> list() {
        return service.listAll();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MembershipType create(@Valid @RequestBody MembershipTypeRequest req) {
        return service.create(req);
    }

    @GetMapping("/{id}")
    public MembershipType get(@PathVariable Long id) {
        return service.getById(id);
    }

    @PutMapping("/{id}")
    public MembershipType update(@PathVariable Long id, @Valid @RequestBody MembershipTypeRequest req) {
        return service.update(id, req);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deactivate(@PathVariable Long id) {
        service.deactivate(id);
    }
}
