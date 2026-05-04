package com.climbup.controller;

import com.climbup.dto.RegisterRequest;
import com.climbup.model.Membership;
import com.climbup.model.MembershipType;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.DiscountService;
import com.climbup.service.MembershipTypeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

import java.util.Map;

@RestController
@RequestMapping("/api/public")
@RequiredArgsConstructor
public class PublicController {

    private final UserRepository userRepo;
    private final MembershipRepository membershipRepo;
    private final MembershipTypeService typeService;
    private final DiscountService discountService;

    @PostMapping("/register")
    @ResponseStatus(HttpStatus.CREATED)
    public Map<String, Object> register(@Valid @RequestBody RegisterRequest req) {
        if (userRepo.existsByEmail(req.email())) {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        }

        User user = new User();
        user.setName(req.name());
        user.setEmail(req.email());
        user.setPhone(req.phone());
        user.setRole(User.Role.MEMBER);
        userRepo.save(user);

        MembershipType type = typeService.getById(req.membershipTypeId());

        Membership membership = new Membership();
        membership.setUser(user);
        membership.setMembershipType(type);
        membership.setPricePaid(discountService.computeEffectivePrice(type));
        membership.setStatus(Membership.Status.PENDING);
        // startDate is intentionally null — set by admin on activation after payment
        membershipRepo.save(membership);

        return Map.of(
            "userId", user.getId(),
            "name", user.getName(),
            "membershipId", membership.getId(),
            "membershipTypeName", type.getName(),
            "status", membership.getStatus().name()
        );
    }
}
