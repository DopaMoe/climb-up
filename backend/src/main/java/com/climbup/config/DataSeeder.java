package com.climbup.config;

import com.climbup.model.GlobalConfig;
import com.climbup.model.MembershipType;
import com.climbup.repository.GlobalConfigRepository;
import com.climbup.repository.MembershipTypeRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.math.BigDecimal;
import java.util.List;

@Configuration
public class DataSeeder {

    @Bean
    CommandLineRunner seed(MembershipTypeRepository typeRepo, GlobalConfigRepository configRepo) {
        return args -> {
            if (typeRepo.count() == 0) {
                typeRepo.saveAll(List.of(
                    type("Monthly Membership", "Full access for 30 days", 200, 30, null, null),
                    type("6 Month Membership", "Full access for 6 months", 1000, 180, null, null),
                    type("12 Month Membership", "Full access for 12 months", 1800, 365, null, null),
                    type("1 Entry", "Single entry pass", 30, null, 1, null),
                    type("10 Entries", "10 entries valid for 6 months", 250, null, 10, 180)
                ));
            }
            if (configRepo.count() == 0) {
                GlobalConfig cfg = new GlobalConfig();
                configRepo.save(cfg);
            }
        };
    }

    private MembershipType type(String name, String desc, int price, Integer durationDays,
                                 Integer entriesCount, Integer entriesValidityDays) {
        MembershipType t = new MembershipType();
        t.setName(name);
        t.setDescription(desc);
        t.setBasePrice(BigDecimal.valueOf(price));
        t.setDurationDays(durationDays);
        t.setEntriesCount(entriesCount);
        t.setEntriesValidityDays(entriesValidityDays);
        return t;
    }
}
