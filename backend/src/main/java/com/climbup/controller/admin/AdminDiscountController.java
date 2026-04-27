package com.climbup.controller.admin;

import com.climbup.dto.DiscountUpdateRequest;
import com.climbup.model.MembershipType;
import com.climbup.repository.MembershipTypeRepository;
import com.climbup.service.DiscountService;
import com.climbup.service.MembershipTypeService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/admin/discounts")
@RequiredArgsConstructor
public class AdminDiscountController {

    private final DiscountService discountService;
    private final MembershipTypeService typeService;
    private final MembershipTypeRepository typeRepo;

    @GetMapping
    public Map<String, Object> get() {
        List<MembershipType> types = typeService.listAll();
        return Map.of(
            "globalDiscount", discountService.getConfig().getGlobalDiscount(),
            "types", types.stream().map(t -> Map.of(
                "id", t.getId(),
                "name", t.getName(),
                "discountPct", t.getDiscountPct()
            )).toList()
        );
    }

    @PutMapping
    public Map<String, Object> update(@RequestBody DiscountUpdateRequest req) {
        if (req.globalDiscount() != null) {
            discountService.updateGlobalDiscount(req.globalDiscount());
        }
        if (req.typeDiscounts() != null) {
            req.typeDiscounts().forEach(td -> {
                MembershipType t = typeService.getById(td.typeId());
                t.setDiscountPct(td.discountPct());
                typeRepo.save(t);
            });
        }
        return get();
    }
}
