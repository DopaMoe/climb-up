package com.climbup.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GymEventLogger {

    @EventListener
    public void onCheckInCompleted(CheckInCompletedEvent event) {
        log.info("[EVENT] CheckInCompleted  — member: {} (id={}) | membership id={}",
            event.checkIn().getUser().getName(),
            event.checkIn().getUser().getId(),
            event.checkIn().getMembership().getId());
    }

    @EventListener
    public void onMembershipExpired(MembershipExpiredEvent event) {
        log.info("[EVENT] MembershipExpired — member: {} (id={}) | membership id={}",
            event.membership().getUser().getName(),
            event.membership().getUser().getId(),
            event.membership().getId());
    }

    @EventListener
    public void onMembershipAssigned(MembershipAssignedEvent event) {
        log.info("[EVENT] MembershipAssigned — member: {} (id={}) | type: {} | status: {}",
            event.membership().getUser().getName(),
            event.membership().getUser().getId(),
            event.membership().getMembershipType().getName(),
            event.membership().getStatus());
    }

    @EventListener
    public void onMembershipActivated(MembershipActivatedEvent event) {
        log.info("[EVENT] MembershipActivated — member: {} (id={}) | membership id={}",
            event.membership().getUser().getName(),
            event.membership().getUser().getId(),
            event.membership().getId());
    }

    @EventListener
    public void onMembershipCancelled(MembershipCancelledEvent event) {
        log.info("[EVENT] MembershipCancelled — member: {} (id={}) | membership id={}",
            event.membership().getUser().getName(),
            event.membership().getUser().getId(),
            event.membership().getId());
    }
}
