package com.climbup.controller.admin;

import com.climbup.dto.ActivateMembershipRequest;
import com.climbup.dto.AssignMembershipRequest;
import com.climbup.dto.MemberRequest;
import com.climbup.model.Membership;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import com.climbup.service.MemberService;
import com.climbup.service.MembershipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
public class AdminMemberController {

    private final MemberService memberService;
    private final MembershipService membershipService;
    private final MembershipRepository membershipRepo;

    @GetMapping("/members")
    public Page<User> list(
        @RequestParam(required = false) String search,
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "20") int size
    ) {
        return memberService.listMembers(search, PageRequest.of(page, size));
    }

    @PostMapping("/members")
    @ResponseStatus(HttpStatus.CREATED)
    public User create(@Valid @RequestBody MemberRequest req) {
        return memberService.create(req);
    }

    @GetMapping("/members/{id}")
    public Map<String, Object> detail(@PathVariable Long id) {
        User user = memberService.getById(id);
        List<Membership> memberships = membershipService.getByUserId(id);
        return Map.of("user", user, "memberships", memberships);
    }

    @PutMapping("/members/{id}")
    public User update(@PathVariable Long id, @Valid @RequestBody MemberRequest req) {
        return memberService.update(id, req);
    }

    @DeleteMapping("/members/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable Long id) {
        memberService.delete(id);
    }

    @PostMapping("/members/{id}/memberships")
    @ResponseStatus(HttpStatus.CREATED)
    public Membership assignMembership(@PathVariable Long id,
                                        @Valid @RequestBody AssignMembershipRequest req) {
        return membershipService.assign(id, req);
    }

    @PutMapping("/memberships/{id}/activate")
    public Membership activate(@PathVariable Long id,
                               @Valid @RequestBody ActivateMembershipRequest req) {
        return membershipService.activate(id, req.startDate());
    }

    @PutMapping("/memberships/{id}/cancel")
    public Membership cancel(@PathVariable Long id) {
        return membershipService.cancel(id);
    }

    @GetMapping("/memberships/pending")
    public List<Map<String, Object>> pendingMemberships(
        @RequestParam(required = false) Long memberId
    ) {
        List<Membership> memberships = memberId != null
            ? membershipRepo.findByStatusAndUserId(Membership.Status.PENDING, memberId)
            : membershipRepo.findByStatus(Membership.Status.PENDING);

        return memberships.stream().map(m -> Map.<String, Object>of(
            "membershipId", m.getId(),
            "userId", m.getUser().getId(),
            "memberName", m.getUser().getName(),
            "membershipTypeName", m.getMembershipType().getName(),
            "createdAt", m.getCreatedAt()
        )).toList();
    }
}
