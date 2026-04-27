package com.climbup.controller.admin;

import com.climbup.model.CheckIn;
import com.climbup.service.CheckInService;
import com.climbup.service.MemberService;
import com.climbup.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/dashboard")
@RequiredArgsConstructor
public class AdminDashboardController {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final CheckInService checkInService;

    @GetMapping
    public Map<String, Object> stats() {
        List<CheckIn> recent = checkInService.getRecent(10);
        return Map.of(
            "totalMembers", memberService.countMembers(),
            "activeMemberships", membershipService.countActive(),
            "pendingMemberships", membershipService.countPending(),
            "checkInsToday", checkInService.countToday(),
            "recentCheckIns", recent.stream().map(c -> Map.of(
                "id", c.getId(),
                "userId", c.getUser().getId(),
                "userName", c.getUser().getName(),
                "membershipType", c.getMembership().getMembershipType().getName(),
                "checkedInAt", c.getCheckedInAt()
            )).toList()
        );
    }
}
