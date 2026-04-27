package com.climbup.controller.member;

import com.climbup.dto.AssignMembershipRequest;
import com.climbup.model.Membership;
import com.climbup.service.DiscountService;
import com.climbup.service.MembershipService;
import com.climbup.service.MembershipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberMembershipController {

    private final MembershipTypeService typeService;
    private final MembershipService membershipService;
    private final DiscountService discountService;

    @GetMapping("/membership-types")
    public List<Map<String, Object>> availableTypes() {
        return typeService.listActive().stream().map(t -> {
            Map<String, Object> m = new LinkedHashMap<>();
            m.put("id", t.getId());
            m.put("name", t.getName());
            m.put("description", t.getDescription());
            m.put("basePrice", t.getBasePrice());
            m.put("effectivePrice", discountService.computeEffectivePrice(t));
            m.put("durationDays", t.getDurationDays());
            m.put("entriesCount", t.getEntriesCount());
            m.put("entriesValidityDays", t.getEntriesValidityDays());
            return m;
        }).toList();
    }

    @PostMapping("/memberships/request")
    @ResponseStatus(HttpStatus.CREATED)
    public Membership request(@RequestParam Long userId,
                               @Valid @RequestBody AssignMembershipRequest req) {
        AssignMembershipRequest pendingReq = new AssignMembershipRequest(
            req.membershipTypeId(), req.startDate(), false
        );
        return membershipService.assign(userId, pendingReq);
    }
}
