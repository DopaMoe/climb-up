package com.climbup.controller.member;

import com.climbup.model.Membership;
import com.climbup.model.User;
import com.climbup.service.CheckInService;
import com.climbup.service.MemberService;
import com.climbup.service.MembershipService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/member")
@RequiredArgsConstructor
public class MemberProfileController {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final CheckInService checkInService;

    @GetMapping("/me")
    public Map<String, Object> me(@RequestParam Long userId) {
        User user = memberService.getById(userId);
        List<Membership> memberships = membershipService.getByUserId(userId);
        return Map.of("user", user, "memberships", memberships);
    }

    @GetMapping("/check-ins")
    public Object checkIns(
        @RequestParam Long userId,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return checkInService.getByUser(userId, PageRequest.of(page, size));
    }
}
