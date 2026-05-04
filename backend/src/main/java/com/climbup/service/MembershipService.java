package com.climbup.service;

import com.climbup.dto.AssignMembershipRequest;
import com.climbup.event.MembershipActivatedEvent;
import com.climbup.event.MembershipAssignedEvent;
import com.climbup.event.MembershipCancelledEvent;
import com.climbup.model.Membership;
import com.climbup.model.MembershipType;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MembershipService {

    private final MembershipRepository repo;
    private final MemberService memberService;
    private final MembershipTypeService typeService;
    private final DiscountService discountService;
    private final ApplicationEventPublisher eventPublisher;

    public List<Membership> getByUserId(Long userId) {
        return repo.findByUserId(userId);
    }

    public List<Membership> getActiveMemberships(Long userId) {
        return repo.findActiveMembershipsByUserId(userId);
    }

    public Membership assign(Long userId, AssignMembershipRequest req) {
        User user = memberService.getById(userId);
        MembershipType type = typeService.getById(req.membershipTypeId());

        Membership m = new Membership();
        m.setUser(user);
        m.setMembershipType(type);
        m.setStartDate(req.startDate());
        m.setPricePaid(discountService.computeEffectivePrice(type));
        m.setStatus(req.directActivate() ? Membership.Status.ACTIVE : Membership.Status.PENDING);
        applyDates(m, type, req.startDate());
        Membership saved = repo.save(m);
        eventPublisher.publishEvent(new MembershipAssignedEvent(saved));
        return saved;
    }

    public Membership activate(Long membershipId) {
        Membership m = getById(membershipId);
        if (m.getStatus() != Membership.Status.PENDING) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Membership is not pending");
        }
        m.setStatus(Membership.Status.ACTIVE);
        Membership saved = repo.save(m);
        eventPublisher.publishEvent(new MembershipActivatedEvent(saved));
        return saved;
    }

    public Membership cancel(Long membershipId) {
        Membership m = getById(membershipId);
        m.setStatus(Membership.Status.CANCELLED);
        Membership saved = repo.save(m);
        eventPublisher.publishEvent(new MembershipCancelledEvent(saved));
        return saved;
    }

    public Membership getById(Long id) {
        return repo.findById(id)
            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Membership not found"));
    }

    private void applyDates(Membership m, MembershipType type, LocalDate startDate) {
        if (type.getDurationDays() != null) {
            m.setEndDate(startDate.plusDays(type.getDurationDays()));
        } else {
            m.setEntriesRemaining(type.getEntriesCount());
            if (type.getEntriesValidityDays() != null) {
                m.setEntriesExpiryDate(startDate.plusDays(type.getEntriesValidityDays()));
            }
        }
    }

    public long countActive() { return repo.countActive(); }
    public long countPending() { return repo.countPending(); }
}
