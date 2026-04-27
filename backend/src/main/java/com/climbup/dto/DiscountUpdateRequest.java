package com.climbup.dto;

import java.util.List;

public record DiscountUpdateRequest(
    Double globalDiscount,
    List<TypeDiscount> typeDiscounts
) {
    public record TypeDiscount(Long typeId, double discountPct) {}
}
