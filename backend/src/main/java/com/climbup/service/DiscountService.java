package com.climbup.service;

import com.climbup.model.GlobalConfig;
import com.climbup.model.MembershipType;
import com.climbup.repository.GlobalConfigRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;

@Service
@RequiredArgsConstructor
public class DiscountService {

    private final GlobalConfigRepository configRepo;

    public GlobalConfig getConfig() {
        return configRepo.findById(1L).orElseGet(() -> {
            GlobalConfig cfg = new GlobalConfig();
            return configRepo.save(cfg);
        });
    }

    public BigDecimal computeEffectivePrice(MembershipType type) {
        double discountPct = type.getDiscountPct() > 0
            ? type.getDiscountPct()
            : getConfig().getGlobalDiscount();
        if (discountPct <= 0) return type.getBasePrice();
        BigDecimal factor = BigDecimal.valueOf(1.0 - discountPct / 100.0);
        return type.getBasePrice().multiply(factor).setScale(2, RoundingMode.HALF_UP);
    }

    public void updateGlobalDiscount(double pct) {
        GlobalConfig cfg = getConfig();
        cfg.setGlobalDiscount(pct);
        configRepo.save(cfg);
    }
}
