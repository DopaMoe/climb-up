package com.climbup.service;

import com.climbup.model.CheckIn;
import com.climbup.model.Membership;
import com.climbup.model.User;
import com.climbup.repository.CheckInRepository;
import com.climbup.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CheckInService {

    private final CheckInRepository checkInRepo;
    private final MembershipRepository membershipRepo;
    private final MemberService memberService;

    @Transactional
    public CheckIn checkIn(Long userId) {
        User user = memberService.getById(userId);
        List<Membership> active = membershipRepo.findActiveMembershipsByUserId(userId);

        Membership usable = active.stream()
            .filter(this::canUse)
            .findFirst()
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
                "No valid active membership found for this member"));

        if (usable.getEntriesRemaining() != null) {
            usable.setEntriesRemaining(usable.getEntriesRemaining() - 1);
            if (usable.getEntriesRemaining() <= 0) {
                usable.setStatus(Membership.Status.EXPIRED);
            }
            membershipRepo.save(usable);
        }

        CheckIn c = new CheckIn();
        c.setUser(user);
        c.setMembership(usable);
        return checkInRepo.save(c);
    }

    private boolean canUse(Membership m) {
        LocalDate today = LocalDate.now();
        if (m.getEndDate() != null) {
            return !today.isAfter(m.getEndDate()) && !today.isBefore(m.getStartDate());
        }
        boolean hasEntries = m.getEntriesRemaining() != null && m.getEntriesRemaining() > 0;
        boolean notExpired = m.getEntriesExpiryDate() == null || !today.isAfter(m.getEntriesExpiryDate());
        return hasEntries && notExpired;
    }

    public Page<CheckIn> getByUser(Long userId, Pageable pageable) {
        return checkInRepo.findByUserIdOrderByCheckedInAtDesc(userId, pageable);
    }

    public long countToday() {
        return checkInRepo.countSince(LocalDateTime.now().toLocalDate().atStartOfDay());
    }

    public List<CheckIn> getRecent(int limit) {
        return checkInRepo.findRecent(PageRequest.of(0, limit));
    }
}
