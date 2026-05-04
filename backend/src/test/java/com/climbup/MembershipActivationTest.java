package com.climbup;

import com.climbup.model.Membership;
import com.climbup.model.MembershipType;
import com.climbup.model.User;
import com.climbup.repository.MembershipRepository;
import com.climbup.repository.MembershipTypeRepository;
import com.climbup.repository.UserRepository;
import com.climbup.service.MembershipService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@Transactional
class MembershipActivationTest {

    @Autowired MembershipService membershipService;
    @Autowired MembershipRepository membershipRepo;
    @Autowired MembershipTypeRepository typeRepo;
    @Autowired UserRepository userRepo;

    @Test
    void activate_setsStartDateAndComputesEndDate() {
        User user = new User();
        user.setName("Test"); user.setEmail("t@t.com");
        userRepo.save(user);

        MembershipType type = new MembershipType();
        type.setName("Monthly"); type.setBasePrice(BigDecimal.valueOf(50));
        type.setDurationDays(30);
        typeRepo.save(type);

        Membership m = new Membership();
        m.setUser(user); m.setMembershipType(type);
        m.setPricePaid(BigDecimal.valueOf(50));
        m.setStatus(Membership.Status.PENDING);
        membershipRepo.save(m);

        LocalDate start = LocalDate.of(2026, 6, 1);
        Membership activated = membershipService.activate(m.getId(), start);

        assertThat(activated.getStatus()).isEqualTo(Membership.Status.ACTIVE);
        assertThat(activated.getStartDate()).isEqualTo(start);
        assertThat(activated.getEndDate()).isEqualTo(LocalDate.of(2026, 7, 1));
    }

    @Test
    void activate_entryBased_setsExpiryDate() {
        User user = new User();
        user.setName("Test2"); user.setEmail("t2@t.com");
        userRepo.save(user);

        MembershipType type = new MembershipType();
        type.setName("10-Pack"); type.setBasePrice(BigDecimal.valueOf(80));
        type.setEntriesCount(10); type.setEntriesValidityDays(60);
        typeRepo.save(type);

        Membership m = new Membership();
        m.setUser(user); m.setMembershipType(type);
        m.setPricePaid(BigDecimal.valueOf(80));
        m.setStatus(Membership.Status.PENDING);
        membershipRepo.save(m);

        LocalDate start = LocalDate.of(2026, 6, 1);
        Membership activated = membershipService.activate(m.getId(), start);

        assertThat(activated.getStatus()).isEqualTo(Membership.Status.ACTIVE);
        assertThat(activated.getStartDate()).isEqualTo(start);
        assertThat(activated.getEntriesExpiryDate()).isEqualTo(LocalDate.of(2026, 7, 31));
    }
}
